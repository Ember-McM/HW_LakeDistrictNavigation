package com.GLAS.LakeDistrictNavigation;

public class RouteRequest {

    private Survey survey;
    private RouteDetails routeDetails;
    private String areInLakes;
    private String date;
    private String mainReason;

    public Survey getSurvey() {return survey;}
    public void setSurvey(Survey survey){
        this.survey = survey;
    }

    public RouteDetails getRouteDetails() {return routeDetails;}
    public void setRouteDetails(RouteDetails routeDetails){
        this.routeDetails = routeDetails;
    }

    public String getAreInLakes() {return areInLakes;}
    public void setAreInLakes(String areInLakes){this.areInLakes = areInLakes;}

    public String getDate() {return date;}
    public void setDate(String date){
        this.date = date;
    }

    public String getMainReason() {return mainReason;}
    public void setMainReason(String mainReason) {this.mainReason = mainReason;}
}
