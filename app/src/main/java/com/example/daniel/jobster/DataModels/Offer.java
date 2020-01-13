package com.example.daniel.jobster.DataModels;

public class Offer
{
    private long id, idUser;
    private int salaryType, applicationsCount;
    private double salary;
    private String offerName, city, startTime, description, providerName;

    public final static int SALARY_TYPE_PIECEWORK = 1;
    public final static int SALARY_TYPE_PER_HOUR = 2;
    public final static int SALARY_TYPE_PER_DAY = 3;
    public final static int SALARY_TYPE_PER_WORK = 4;

    public final static int STATUS_INACTIVE = 0;
    public final static int STATUS_ACTIVE = 1;

    // constructor for listview (OffersExplorer -> getMyOffers)
    public Offer(long id, String offerName, String city, String startTime, double salary, int salaryType)
    {
        this.id = id;
        this.offerName = offerName;
        this.city = city;
        this.startTime = startTime;
        this.salary = salary;
        this.salaryType = salaryType;
    }

    // constructor for listview (OffersExplorer -> getAllOffers)
    public Offer(long id, String offerName, String city, String startTime, double salary, int salaryType, String providerName)
    {
        this.id = id;
        this.offerName = offerName;
        this.city = city;
        this.startTime = startTime;
        this.salary = salary;
        this.salaryType = salaryType;
        this.providerName = providerName;
    }


    public long getId() {
        return id;
    }

    public long getIdUser() {
        return idUser;
    }

    public double getSalary() {
        return salary;
    }

    public String getOfferName() {
        return offerName;
    }

    public String getCity() {
        return city;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getDescription() {
        return description;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getSalaryTypeString()
    {
        switch(salaryType)
        {
            case SALARY_TYPE_PIECEWORK:
                return "na akord";

            case SALARY_TYPE_PER_HOUR:
                return "za godzinę";

            case SALARY_TYPE_PER_DAY:
                return "za dzień";

            case SALARY_TYPE_PER_WORK:
                return "za całość";
        }

        return "";
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
