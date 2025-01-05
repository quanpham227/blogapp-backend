-- Migration to remove unique constraint from public_id in posts table

ALTER TABLE `posts` DROP INDEX `UK_2bt51s180t1gbfp1n1f85srsv`;