package com.example.getit;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherData {
    private String temperature , icon, city, weatherType;
    private int condition;
    public static WeatherData fromJason(JSONObject jsonObject){
        try {
           WeatherData weatherData  = new WeatherData();
           weatherData.city = jsonObject.getString("name-");
           weatherData.condition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
           weatherData.weatherType = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
           weatherData.icon = updateWeatherIcon(weatherData.condition);
           double tempResult = jsonObject.getJSONObject("main").getDouble("temp")-273.15;
           int roundedValue = (int) Math.rint(tempResult);
           weatherData.temperature = Integer.toString(roundedValue);
           return  weatherData;
        }
        catch (JSONException exception){
            exception.printStackTrace();
            return null;
        }
    }
    private static String updateWeatherIcon(int condition){
        if(condition >= 100 && condition < 300)
            return "logos_19";
        else if(condition >= 300 && condition <= 321)
            return "logos_11";
        else if(condition >= 500 && condition <= 531)
            return "logos_5";
        else if(condition >= 600 && condition <= 622)
            return "logos_23";
        else if(condition >= 701 && condition <= 781)
            return "logos_22";
        else if(condition == 800)
            return "logos_3";
        else if(condition >= 801 && condition <= 804)
            return "logo_7";
        else return "logo_3";
    }


    public String getTemperature() {
        return temperature + "°C";
    }

    public String getIcon() {
        return icon;
    }

    public String getCity() {
        return city;
    }

    public String getWeatherType() {
        return weatherType;
    }
}
