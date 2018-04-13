package macaulish.gis;

/**
 * 包含经纬度海拔和速度信息
 */
public class Location {
    private static final char EAST_FLAG = 'E';
    private static final char WEST_FLAG = 'W';
    private static final char SOUTH_FLAG = 'S';
    private static final char NORTH_FLAG = 'N';

    //经度的三个分量：度分秒
    private int longitude_degrees;
    private int longitude_minutes;
    private int longitude_seconds;
    //经度的方向
    private char EorW;
    //纬度的三个分量：度分秒
    private int latitude_degrees;
    private int latitude_minutes;
    private int latitude_seconds;
    //纬度的方向
    private char NorS;
    //海拔：单位(米)
    private int altitude;
    //速度：单位(米/秒)
    private float speed;

    /**
     * dddff.ffff格式的经纬度
     * 前三位为度,后两位为分
     * 小数部分*60取整为秒，其它参数默认为0
     * @param longitude dddff.ffff格式的经度
     * @param latitude dddff.ffff格式的纬度
     */
    public Location(float longitude,float latitude){
        longitude_degrees = (int) (longitude/100);
        longitude_minutes = (int) (longitude%100);
        longitude_seconds = (int) ((longitude - (int)latitude)*60);
        if(longitude_degrees < 0){
            EorW = WEST_FLAG;
            longitude_degrees *= -1;
        } else {
            EorW = EAST_FLAG;
        }
        latitude_degrees = (int) (latitude/100);
        latitude_minutes = (int) (latitude%100);
        latitude_seconds = (int) ((latitude - (int)latitude)*60);
        if(latitude_degrees < 0){
            NorS = SOUTH_FLAG;
            longitude_degrees *= -1;
        } else {
            NorS = NORTH_FLAG;
        }
    }

    /**
     * dddff.ffff格式的经纬度
     * 前三位为度,后两位为分
     * 小数部分*60取整为秒
     * 海拔高度和速度也通过参数传给构造器
     * @param longitude dddff.ffff格式的经度
     * @param latitude dddff.ffff格式的纬度
     * @param altitude 海拔高度
     * @param speed 速度
     */
    public Location(float longitude,float latitude,int altitude,float speed){
        this(longitude,latitude);
        this.altitude = altitude;
        this.speed = speed;
    }

    @Override
    public String toString() {
        //示例：121°20′576″E 20°42′930″N 100m 12.31m/s
        return  longitude_degrees+'°'+longitude_minutes+'′'+longitude_seconds+'″'+EorW+ ' '+
                latitude_degrees+'°'+latitude_minutes+'′'+latitude_seconds+'″'+NorS+' '+
                altitude+'m'+' '+speed+"m/s\n";
    }

    public int getLongitudeDegrees() {
        return longitude_degrees;
    }

    public void setLongitudeDegrees(int longitudeDegrees) {
        this.longitude_degrees = longitudeDegrees;
    }

    public int getLongitudeMinutes() {
        return longitude_minutes;
    }

    public void setLongitudeMinutes(int longitudeMinutes) {
        this.longitude_minutes = longitudeMinutes;
    }

    public int getLongitudeSeconds() {
        return longitude_seconds;
    }

    public void setLongitudeSeconds(int longitudeSeconds) {
        this.longitude_seconds = longitudeSeconds;
    }

    public char getEorW() {
        return EorW;
    }

    public void setEorW(char EorW) {
        this.EorW = EorW;
    }

    public int getLatitudeDegrees() {
        return latitude_degrees;
    }

    public void setLatitudeDegrees(int latitudeDegrees) {
        this.latitude_degrees = latitudeDegrees;
    }

    public int getLatitudeMinutes() {
        return latitude_minutes;
    }

    public void setLatitudeMinutes(int latitudeMinutes) {
        this.latitude_minutes = latitudeMinutes;
    }

    public int getLatitudeSeconds() {
        return latitude_seconds;
    }

    public void setLatitudeSeconds(int latitudeSeconds) {
        this.latitude_seconds = latitudeSeconds;
    }

    public char getNorS() {
        return NorS;
    }

    public void setNorS(char NorS) {
        this.NorS = NorS;
    }

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
