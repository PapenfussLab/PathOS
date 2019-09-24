ALTER TABLE seq_variant ADD max_pm_class int(11);
ALTER TABLE seq_variant ADD acmg_sort int(11);
ALTER TABLE seq_variant ADD amp_sort int(11);
ALTER TABLE seq_variant ADD overall_sort int(11);
alter table cur_variant drop index clin_context_id;
