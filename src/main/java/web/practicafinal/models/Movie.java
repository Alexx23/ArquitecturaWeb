/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package web.practicafinal.models;

import com.google.gson.annotations.Expose;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author Alex
 */
@Entity
@Table(name = "movie")
@NamedQueries({
    @NamedQuery(name = "Movie.findAll", query = "SELECT m FROM Movie m"),
    @NamedQuery(name = "Movie.findById", query = "SELECT m FROM Movie m WHERE m.id = :id"),
    @NamedQuery(name = "Movie.findByName", query = "SELECT m FROM Movie m WHERE m.name = :name"),
    @NamedQuery(name = "Movie.findByWeb", query = "SELECT m FROM Movie m WHERE m.web = :web"),
    @NamedQuery(name = "Movie.findByOriginalTitle", query = "SELECT m FROM Movie m WHERE m.originalTitle = :originalTitle"),
    @NamedQuery(name = "Movie.findByDuration", query = "SELECT m FROM Movie m WHERE m.duration = :duration"),
    @NamedQuery(name = "Movie.findByYear", query = "SELECT m FROM Movie m WHERE m.year = :year")})
public class Movie implements Serializable {

    @Expose
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    @Expose
    private Integer id;
    @Basic(optional = false)
    @Column(name = "name")
    @Expose
    private String name;
    @Basic(optional = false)
    @Column(name = "web")
    @Expose
    private String web;
    @Basic(optional = false)
    @Column(name = "original_title")
    @Expose
    private String originalTitle;
    @Basic(optional = false)
    @Column(name = "duration")
    @Expose
    private short duration;
    @Basic(optional = false)
    @Column(name = "year")
    @Expose
    private short year;
    @ManyToMany(mappedBy = "movieCollection")
    @Expose
    private Collection<Label> labelCollection;
    @ManyToMany(mappedBy = "movieCollection")
    @Expose
    private Collection<Actor> actorCollection;
    @JoinColumn(name = "age_classification_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    @Expose
    private AgeClassification ageClassificationId;
    @JoinColumn(name = "director_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    @Expose
    private Director directorId;
    @JoinColumn(name = "distributor_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    @Expose
    private Distributor distributorId;
    @JoinColumn(name = "genre_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    @Expose
    private Genre genreId;
    @JoinColumn(name = "nationality_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    @Expose
    private Nationality nationalityId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "movieId")
    @Expose
    private Collection<Session> sessionCollection;

    public Movie() {
    }

    public Movie(Integer id) {
        this.id = id;
    }

    public Movie(Integer id, String name, String web, String originalTitle, short duration, short year) {
        this.id = id;
        this.name = name;
        this.web = web;
        this.originalTitle = originalTitle;
        this.duration = duration;
        this.year = year;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public short getDuration() {
        return duration;
    }

    public void setDuration(short duration) {
        this.duration = duration;
    }

    public short getYear() {
        return year;
    }

    public void setYear(short year) {
        this.year = year;
    }

    public Collection<Label> getLabelCollection() {
        return labelCollection;
    }

    public void setLabelCollection(Collection<Label> labelCollection) {
        this.labelCollection = labelCollection;
    }

    public Collection<Actor> getActorCollection() {
        return actorCollection;
    }

    public void setActorCollection(Collection<Actor> actorCollection) {
        this.actorCollection = actorCollection;
    }

    public AgeClassification getAgeClassificationId() {
        return ageClassificationId;
    }

    public void setAgeClassificationId(AgeClassification ageClassificationId) {
        this.ageClassificationId = ageClassificationId;
    }

    public Director getDirectorId() {
        return directorId;
    }

    public void setDirectorId(Director directorId) {
        this.directorId = directorId;
    }

    public Distributor getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(Distributor distributorId) {
        this.distributorId = distributorId;
    }

    public Genre getGenreId() {
        return genreId;
    }

    public void setGenreId(Genre genreId) {
        this.genreId = genreId;
    }

    public Nationality getNationalityId() {
        return nationalityId;
    }

    public void setNationalityId(Nationality nationalityId) {
        this.nationalityId = nationalityId;
    }

    public Collection<Session> getSessionCollection() {
        return sessionCollection;
    }

    public void setSessionCollection(Collection<Session> sessionCollection) {
        this.sessionCollection = sessionCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Movie)) {
            return false;
        }
        Movie other = (Movie) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "web.practicafinal.models.Movie[ id=" + id + " ]";
    }
    
}
