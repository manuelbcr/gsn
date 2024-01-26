package ch.epfl.gsn.storage.db;

import static org.junit.Assert.*;
import org.junit.Test;
import java.sql.*;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;


public class PostgresStorageManagerTest{
    @Test
    public void testMySQLStorageManager() throws SQLException{
        PostgresStorageManager sm= new PostgresStorageManager();
        assertEquals("jdbc:postgresql:",sm.getJDBCPrefix());

        assertEquals("DROP TABLE IF EXISTS #NAME",sm.getStatementDropIndex());
        assertEquals("DROP VIEW IF EXISTS #NAME",sm.getStatementDropView());
        assertEquals(0,sm.getTableNotExistsErrNo()); //todo check error code in PostgresReference
        assertEquals("select * FROM xy LIMIT 10 OFFSET 10",sm.addLimit("select * FROM xy",10,10));
        assertEquals("SELECT extract(epoch FROM now())*1000", sm.getStatementDifferenceTimeInMillis());
        assertEquals("Drop table if exists xy", sm.getStatementDropTable("xy",null).toString());

        DataField[] fields = new DataField[]{
            new DataField("value", DataTypes.INTEGER),
            new DataField("value1", DataTypes.INTEGER)
        };

        assertEquals("CREATE TABLE xy (PK serial PRIMARY KEY NOT NULL , timed BIGINT NOT NULL, VALUE Integer ,VALUE1 Integer)",sm.getStatementCreateTable("xy",fields).toString());
        assertEquals("delete from xy where xy.timed <= ( SELECT * FROM ( SELECT timed FROM xy group by xy.timed ORDER BY xy.timed DESC LIMIT 1 offset 10  ) AS TMP)",sm.getStatementUselessDataRemoval("xy",10L).toString());
        assertEquals("delete from xy where xy.timed <= ( SELECT * FROM ( SELECT timed FROM xy group by xy.timed ORDER BY xy.timed DESC LIMIT 1 offset 10  ) AS TMP)",sm.getStatementRemoveUselessDataCountBased("xy",10L).toString());
        assertEquals("Char",sm.convertGSNTypeToLocalType(new DataField("value", DataTypes.CHAR)));
        assertEquals("BYTEA",sm.convertGSNTypeToLocalType(new DataField("value", DataTypes.BINARY)));
        assertEquals("DOUBLE PRECISION",sm.convertGSNTypeToLocalType(new DataField("value", DataTypes.DOUBLE)));
        assertEquals("REAL",sm.convertGSNTypeToLocalType(new DataField("value", DataTypes.FLOAT)));
        assertEquals("SMALLINT",sm.convertGSNTypeToLocalType(new DataField("value", DataTypes.TINYINT)));
        assertEquals(DataTypes.BIGINT,sm.convertLocalTypeToGSN(Types.BIGINT,1));
        assertEquals(DataTypes.INTEGER,sm.convertLocalTypeToGSN(Types.INTEGER,1));
        assertEquals(DataTypes.SMALLINT,sm.convertLocalTypeToGSN(Types.SMALLINT,1));
        assertEquals(DataTypes.TINYINT,sm.convertLocalTypeToGSN(Types.TINYINT,1));
        assertEquals(DataTypes.VARCHAR,sm.convertLocalTypeToGSN(Types.VARCHAR,1));
        assertEquals(DataTypes.CHAR,sm.convertLocalTypeToGSN(Types.CHAR,1));
        assertEquals(DataTypes.FLOAT,sm.convertLocalTypeToGSN(Types.REAL,1));
        assertEquals(-100,sm.convertLocalTypeToGSN(Types.NULL,1));
        assertEquals(DataTypes.DOUBLE,sm.convertLocalTypeToGSN(Types.DOUBLE,1));
        assertEquals(DataTypes.BINARY,sm.convertLocalTypeToGSN(Types.BLOB,1));
    }
}