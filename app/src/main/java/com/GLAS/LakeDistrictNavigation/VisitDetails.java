package com.GLAS.LakeDistrictNavigation;

public class VisitDetails
{
    private Survey survey;
    private String visitPlace;
    private String visitType;

    private String date;
    private String areInLakes;

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

    public String getAreInLakes() {return areInLakes;}
    public void setAreInLakes(String areInLakes){this.areInLakes = areInLakes;}


    public String getDate() {return date;}
    public void setDate(String date){
        this.date = date;
    }


}
