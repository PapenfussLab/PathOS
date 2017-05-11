PathOS Build
============

## Available tags

- `latest`, `1`: Build environment for PathOS 1.x

## About this image

This image contains a build environment for the PathOS variant curation platform.

## How to use this image

For a one-off build, the only command line addition is a volume to store the built artifacts.

```bash
$ mkdir pathos
$ docker run --rm -it -v $(pwd)/pathos/:/pathos/ pathos-build
```

This will build the requisite artifacts and leave them in the
`pathos` directory.

The build process must download a large number of dependencies,
which takes a substantial amount of time. If repeated builds are
expected, the repeated downloading of dependencies may be avoided
by creating a *data* *container* to store the cached dependencies
before running the `pathos-build` container.

```bash
$ docker create -v /cache --name pathos-build-cache pathos-build
```

Then, to use the *data* *container*, we mention it when we run the build container:

```bash
$ docker run --rm -it -v $(pwd)/pathos/:/pathos/ --volumes-from pathos-build-cache pathos-build
```

### Proxy Setting

Getting Docker things running through a proxy behind a firewall can
be a bit tricky.  The first observation is that different parts of
the software stack use different methods for communicating proxy
settings.  If you wish to set a proxy (we assume the same proxy can
be host for both http and https requests), you can use the
`PATHOS_PROXY_HOST` and `PATHOS_PROXY_PORT` environment variables.

The the next thing to realize is that on some host platforms (e.g.
OSX), the Docker containers run inside a (Linux) virtual machine,
so the hostname `localhost` refers not to the host (i.e. the Mac),
but to the virtual machine, so if a local authenticating proxy is
used, setting the `http[s]_proxy` environment variable to a value
like `localhost:3128` does not work as expected.

Consequently, if you are using an authenticating proxy, such as
`cntlm`, you will need to have an IP address for accessing your
proxy that is visible from the containers.  This can be obtained
from `ifconfig`:

```bash
lo0: flags=8049<UP,LOOPBACK,RUNNING,MULTICAST> mtu 16384
        options=1203<RXCSUM,TXCSUM,TXSTATUS,SW_TIMESTAMP>
        inet 127.0.0.1 netmask 0xff000000 
        inet6 ::1 prefixlen 128 
        inet6 fe80::1%lo0 prefixlen 64 scopeid 0x1 
        nd6 options=201<PERFORMNUD,DAD>
en0: flags=8863<UP,BROADCAST,SMART,RUNNING,SIMPLEX,MULTICAST> mtu 1500
        ether f4:0f:24:34:9e:84 
        inet6 fe80::cf1:4e58:3f11:2d6b%en0 prefixlen 64 secured scopeid 0x6 
        inet 10.126.67.101 netmask 0xffffff00 broadcast 10.126.67.255
        nd6 options=201<PERFORMNUD,DAD>
        media: autoselect
        status: active
en1: flags=963<UP,BROADCAST,SMART,RUNNING,PROMISC,SIMPLEX> mtu 1500
        options=60<TSO4,TSO6>
        ether 06:00:35:59:b1:00 
        media: autoselect <full-duplex>
        status: inactive

```
In this case, the appropiate IP address to use would be `10.126.67.101`.


### Environment Variables

If an alternate git repository is desired, the URL to it can be put
in the `PATHOS_GIT` environment variable.  This may require
authentication to be specified in the URL, though if the container
is run in interactive mode, credentials can be supplied. Alternitavely,
a volume may be used to overwrite `/root/.ssh/id_rsa` with an
alternative private key.

If the environment variable `PATHOS_BRANCH` is specified, the clone
of the repository will checkout the named branch before the build
is launched.

If a proxy is required for HTTP[s] then the `PATHOS_PROXY_HOST` and
`PATHOS_PROXY_PORT` variables should be used. See above.

## Deploying the artifacts.

The results of the container can trivially be deployed using the
docker-compose infrastructure of which the build environment is
part. If you have run the `pathos-build` container with a `pathos`
volume as described above from the current directory (`pathos-build`),
the following commands will put the artifacts in place for
deploying PathOS.

```bash
$ cd pathos
$ cp PathOS.war ../../pathos-tomcat/
$ tar czvf ../../pathos-tools/tools.tgz bin etc lib
```

So you can then:

```bash
$ cd ../..
$ docker-compose build
$ docker-compose up
```
