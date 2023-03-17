package com.GLAS.LakeDistrictNavigation;

public class VisitDetails
{
    private Survey survey;
    private String visitPlace;
    private String visitType;
    private RequestLocation requestLocation;
    private String date;

    public Survey getSurvey() {return survey;}
    public void setSurvey(Survey survey){
        this.survey = survey;
    }

    public String getVisitPlace() {return visitPlace;}
    public void setVisitPlace(String visitPlace){
        this.visitPlace = visitPlace;
    }

    public String getVisitType() {return visitType;}
    public void setVisitType(String visitType) {this.visitType = visitType;}

    public RequestLocation getRequestLocation() {return requestLocation;}
    public void setRequestLocation(RequestLocation requestLocation){
        this.requestLocation = requestLocation;
    }

    public String getDate() {return date;}
    public void setDate(String date){
        this.date = date;
    }


}
