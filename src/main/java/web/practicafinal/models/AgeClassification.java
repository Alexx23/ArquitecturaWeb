/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package web.practicafinal.models;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "age_classification")
@NamedQueries({
    @NamedQuery(name = "AgeClassification.findAll", query = "SELECT a FROM AgeClassification a"),
    @NamedQuery(name = "AgeClassification.findById", query = "SELECT a FROM AgeClassification a WHERE a.id = :id"),
    @NamedQuery(name = "AgeClassification.findByName", query = "SELECT a FROM AgeClassification a WHERE a.name = :name"),
    @NamedQuery(name = "AgeClassification.findByAge", query = "SELECT a FROM AgeClassification a WHERE a.age = :age")})
public class AgeClassification implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "age")
    private short age;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ageClassificationId")
    private Collection<Movie> movieCollection;

    public AgeClassification() {
    }

    public AgeClassification(Integer id) {
        this.id = id;
    }

    public AgeClassification(Integer id, String name, short age) {
        this.id = id;
        this.name = name;
        this.age = age;
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

    public short getAge() {
        return age;
    }

    public void setAge(short age) {
        this.age = age;
    }

    public Collection<Movie> getMovieCollection() {
        return movieCollection;
    }

    public void setMovieCollection(Collection<Movie> movieCollection) {
        this.movieCollection = movieCollection;
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
        if (!(object instanceof AgeClassification)) {
            return false;
        }
        AgeClassification other = (AgeClassification) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "web.practicafinal.models.AgeClassification[ id=" + id + " ]";
    }
    
}
