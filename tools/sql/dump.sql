select TABLE_NAME, TABLE_ROWS
from information_schema.TABLES
where TABLE_SCHEMA = 'test'
order by TABLE_ROWS desc;

