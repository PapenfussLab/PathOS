import datetime
import random
import sys
import yaml

class rndCtxt(object):
    def __init__(self, key):
        self.hx = 19 + hash(key)
        self.state = None

    def __enter__(self):
        self.state = random.getstate()
        random.seed(self.hx)

    def __exit__(self, *args):
        random.setstate(self.state)

names = {}
with open('names.yaml') as f:
    names = yaml.load(f)

def mkFamilyName():
    r = []
    while True:
        r.append(random.choice(names['family']))
        if random.random() > 0.2:
            break
    return '-'.join(r)

def mkNameParts(g):
    if g == 'M':
        return (mkFamilyName(), random.choice(names['male']))
    if g == 'F':
        return (mkFamilyName(), random.choice(names['female']))
    if random.random() < 0.5:
        return (mkFamilyName(), random.choice(names['male']))
    else:
        return (mkFamilyName(), random.choice(names['female']))

def mkName(g):
    (fam, gvn) = mkNameParts(g)
    return '%s,%s' % (fam, gvn)

renames = {}

def mkAuthName(oldName):
    if oldName not in renames:
        usr = {}
        with rndCtxt(oldName):
            (fam, gvn) = mkNameParts('U')
        usr['displayName'] = '%s %s' % (gvn, fam)
        usr['username'] = '%s%s' % (fam, gvn)
        usr['email'] = '%s.%s@example.com' % (gvn, fam)
        renames[oldName] = usr
    return renames[oldName]

doctors = [mkName('U') for i in range(50)]

def mkDob():
    n = random.triangular(1930.0, 2000.0, 1955.0)
    y = int(n)
    n = 12.0 * (n - y)
    m = int(n)
    n = 28.0 * (n - m)
    d = int(n)
    m += 1
    d += 1
    a = (datetime.date.today() - datetime.date(y, m, d)).days // 365
    return ('%04d%02d%02d' % (y, m, d), a)

def mangle(rec):
    if rec['domain'] == 'patient':
        for i in range(len(rec['data'])):
            itm = rec['data'][i]
            if 'patient' in itm:
                pat = itm['patient']
                nm = mkName(pat['sex'])
                (dob, age) = mkDob()
                pat['name'] = nm
                pat['dob'] = dob
                pat['age'] = age
            if 'patSample' in itm:
                itm['patSample']['requester'] = random.choice(doctors)
                u = mkAuthName(itm['patSample']['owner'])
                itm['patSample']['owner'] = u['username']

    if rec['domain'] == 'sequence':
        kill = []
        for i in range(len(rec['data'])):
            itm = rec['data'][i]
            if 'seqSample' in itm:
                seq = itm['seqSample']
                u = mkAuthName(seq['userName'])
                seq['userName'] = u['username']
                seq['userEmail'] = u['email']
                if 'authorisedQc' in seq:
                    u = mkAuthName(seq['authorisedQc'])
                    seq['authorisedQc'] = u['username']
                if 'firstReviewBy' in seq:
                    u = mkAuthName(seq['firstReviewBy'])
                    seq['firstReviewBy'] = u['username']
                if 'secondReviewBy' in seq:
                    u = mkAuthName(seq['secondReviewBy'])
                    seq['secondReviewBy'] = u['username']
                if 'finalReviewBy' in seq:
                    u = mkAuthName(seq['finalReviewBy'])
                    seq['finalReviewBy'] = u['username']
            if 'seqRun' in itm:
                seq = itm['seqRun']
                if 'authorised' in seq:
                    u = mkAuthName(seq['authorised'])
                    seq['authorised'] = u['username']
            #if 'seqVariant' in itm:
            #    kill.append(i)
        while len(kill) > 0:
            i = kill.pop()
            del rec['data'][i]

    if rec['domain'] == 'user':
        for i in range(len(rec['data'])):
            itm = rec['data'][i]
            if 'authUser' in itm:
                usr = itm['authUser']
                rename = mkAuthName(usr['username'])
                usr['displayName'] = rename['displayName']
                usr['username'] = rename['username']
                usr['email'] = rename['email']

    if rec['domain'] == 'curation':
        for i in range(len(rec['data'])):
            itm = rec['data'][i]
            if 'clinContext' in itm:
                if 'createdBy' in itm['clinContext']:
                    rename = mkAuthName(itm['clinContext']['createdBy'])
                    itm['clinContext']['createdBy'] = rename['username']
            if 'curVariant' in itm:
                cur = itm['curVariant']
                if 'classified' in cur:
                    rename = mkAuthName(cur['classified'])
                    cur['classified'] = rename['username']
                if 'authorised' in cur:
                    rename = mkAuthName(cur['authorised'])
                    cur['authorised'] = rename['username']
    return len(rec['data']) > 0

def allrecords() :
    for rec in yaml.load_all(sys.stdin):
        ok = mangle(rec)
        if ok:
            yield rec

yaml.safe_dump_all(allrecords(), sys.stdout)
