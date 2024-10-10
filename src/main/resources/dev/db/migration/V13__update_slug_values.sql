-- V13__update_slug_values.sql

UPDATE tags SET slug = CONCAT('slug-', id) WHERE slug = 'temp-slug';