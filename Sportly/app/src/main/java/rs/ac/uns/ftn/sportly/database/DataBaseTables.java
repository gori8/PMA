package rs.ac.uns.ftn.sportly.database;

public class DataBaseTables {
    public static final String ID = "_id";
    public static final String SERVER_ID = "server_id";

    //FRIENDS
    public static final String TABLE_FRIENDS = "friends";
    public static final String FRIENDS_FIRST_NAME = "first_name";
    public static final String FRIENDS_LAST_NAME = "last_name";
    public static final String FRIENDS_EMAIL = "email";
    public static final String FRIENDS_USERNAME = "username";


    public static final String TABLE_NOTIFICATIONS = "notifications";

    //SPORTSFIELDS
    public static final String TABLE_SPORTSFIELDS = "sportsfields";
    public static final String SPORTSFIELDS_DESCRIPTION = "description";
    public static final String SPORTSFIELDS_LATITUDE = "latitude";
    public static final String SPORTSFIELDS_LONGITUDE = "longitude";
    public static final String SPORTSFIELDS_NAME = "name";

    //EVENTS
    public static final String TABLE_EVENTS = "events";
    public static final String EVENTS_CURR = "curr";
    public static final String EVENTS_NUMB_OF_PPL = "numb_of_ppl";
    public static final String EVENTS_PRICE = "price";
    public static final String EVENTS_TIME_FROM = "time_from";
    public static final String EVENTS_TIME_TO = "time_to";
    public static final String EVENTS_SPORTS_FILED_ID = "sports_field_id";

    public static final String TABLE_FAVORITES = "favorites";
    public static final String TABLE_MY_EVENTS = "my_events";
    public static final String TABLE_PARTICIPATING_EVENTS = "participating_events";

    public static final String FRIENDS_CREATE = "CREATE TABLE "+DataBaseTables.TABLE_FRIENDS+"("
            + ID + " integer primary key autoincrement ,"
            + FRIENDS_FIRST_NAME + " text, "
            + FRIENDS_LAST_NAME + " text, "
            + FRIENDS_USERNAME + " text, "
            + FRIENDS_EMAIL + " text, "
            + SERVER_ID + " INTEGER NOT NULL, UNIQUE("+SERVER_ID+"))";

    public static final String SPORTSFIELDS_CREATE = "CREATE TABLE "+TABLE_SPORTSFIELDS+"("
            + ID + " integer primary key autoincrement ,"
            + SPORTSFIELDS_DESCRIPTION + " text, "
            + SPORTSFIELDS_LATITUDE + " real, "
            + SPORTSFIELDS_LONGITUDE + " real, "
            + SPORTSFIELDS_NAME + " text, "
            + SERVER_ID + " INTEGER NOT NULL, UNIQUE("+SERVER_ID+"))";

    public static final String EVENTS_CREATE = "CREATE TABLE "+TABLE_EVENTS+"("
            + ID + " integer primary key autoincrement ,"
            + EVENTS_CURR + " text, "
            + EVENTS_PRICE + " real, "
            + EVENTS_NUMB_OF_PPL + " integer, "
            + EVENTS_SPORTS_FILED_ID + " integer, "
            + EVENTS_TIME_FROM + " datetime, "
            + EVENTS_TIME_TO + " datetime, "
            + SERVER_ID + " INTEGER NOT NULL, UNIQUE("+SERVER_ID+"))";
}
