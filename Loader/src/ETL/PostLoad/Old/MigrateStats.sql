/*	Create mp_webtest schema AlignStats table
**
**	kdd		10-Nov-2013
**
*/

drop table if exists align_stats;

create table align_stats (id bigint(20) not null auto_increment, primary key (id))
as
select	*
from	mp_alignstats
;

create index align_stats_idx1 on align_stats(seqrun);
create index align_stats_idx2 on align_stats(sample);
create index align_stats_idx3 on align_stats(panel);
