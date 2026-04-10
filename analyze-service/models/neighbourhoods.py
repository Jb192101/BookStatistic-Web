import sqlalchemy
from sqlalchemy.dialects.postgresql import UUID

metadata = sqlalchemy.MetaData()

similarity_table = sqlalchemy.Table()