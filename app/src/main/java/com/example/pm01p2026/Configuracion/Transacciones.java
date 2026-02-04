package com.example.pm01p2026.Configuracion;

public class Transacciones
{
    //DB NAME
    public static final String dbname = "DBPM01";

    public static final int dbversion = 1;
    //DB TABLE

    public static final String tbpersons = "personas";

    public static final String id = "id";
    public static final String nombre = "nombre";
    public static final String apellido = "iapellido";
    public static final String edad = "edad";
    public static final String correo = "correo";
    public static final String foto = "foto";

    //DDL Create

    public static final String CreateTablePerson = "CREATE TABLE " + tbpersons + " ( " +

            id + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
            nombre + " TEXT , " +
            apellido + " TEXT , " +
            edad + " INTEGER , " +
            correo + " TEXT , " +
            foto   + " TEXT  )" ;

    //DDL Drop
    public static final String DropTablePerson = "DROP TABLE IF Exts" + tbpersons;

    //DML
    public static final String SelecTablePerson = "SELECT * FROM" + tbpersons;


}
