package com.GLAS.LakeDistrictNavigation;

public class EmojiRate {

    private Survey survey;
    private RouteDetails routeDetails;
    private RequestLocation requestLocation;
    private String date;
    private String emoji;

    public Survey getSurvey() {return survey;}
    public void setSurvey(Survey survey){
        this.survey = survey;
    }

    public RouteDetails getRouteDetails() {return routeDetails;}
    public void setRouteDetails(RouteDetails routeDetails){
        this.routeDetails = routeDetails;
    }

    public RequestLocation getRequestLocation() {return requestLocation;}
    public void setRequestLocation(RequestLocation requestLocation){
        this.requestLocation = requestLocation;
    }

    public String getDate() {return date;}
    public void setDate(String date){
        this.date = date;
    }

    public String getEmoji() {return emoji;}
    public void setEmoji(String emoji) {this.emoji = emoji;}

}
