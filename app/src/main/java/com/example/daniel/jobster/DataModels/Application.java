package com.example.daniel.jobster.DataModels;

public class Application
{
    private long id, idOffer, idUser;
    private int salaryType, status;
    private double salary;
    private String offerName, city, applicationTime, description, applicantName, message;

    public final static int STATUS_WAITING = 1;
    public final static int STATUS_ACCEPTED = 2;
    public final static int STATUS_REJECTED = 3;

    // constructor for listview (ApplicationsExplorer)
    public Application(long id, String offerName, String city, int status, double salary, int salaryType, String applicationTime, long idOffer, long idUser)
    {
        this.id = id;
        this.offerName = offerName;
        this.city = city;
        this.status = status;
        this.salary = salary;
        this.salaryType = salaryType;
        this.applicationTime = applicationTime;
        this.idOffer = idOffer;
        this.idUser = idUser;
    }

    // constructor for listview (OfferApplicationsExplorer)
    public Application(long id, String applicantName, String applicationTime, int status, String message, long idOffer, long idApplier)
    {
        this.id = id;
        this.applicantName = applicantName;
        this.applicationTime = applicationTime;
        this.status = status;
        this.message = message;
        this.idOffer = idOffer;
        this.idUser = idApplier;
    }

    public long getId() {
        return id;
    }

    public long getIdOffer() {
        return idOffer;
    }

    public long getIdUser() {
        return idUser;
    }

    public int getSalaryType() {
        return salaryType;
    }

    public String getSalaryTypeString()
    {
        switch(salaryType)
        {
            case Offer.SALARY_TYPE_PIECEWORK:
                return "na akord";

            case Offer.SALARY_TYPE_PER_HOUR:
                return "za godzinę";

            case Offer.SALARY_TYPE_PER_DAY:
                return "za dzień";

            case Offer.SALARY_TYPE_PER_WORK:
                return "za całość";
        }

        return "";
    }

    public int getStatus() {
        return status;
    }

    public String getStatusString()
    {
        switch(status)
        {
            case STATUS_WAITING:
                return "oczekująca";

            case STATUS_ACCEPTED:
                return "zaakceptowana";

            case STATUS_REJECTED:
                return "odrzucona";
        }

        return "";
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

    public String getApplicationTime() {
        return applicationTime;
    }

    public String getDescription() {
        return description;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public String getMessage() {
        return message;
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

    public void setMessage(String message) {
        this.message = message;
    }
}
