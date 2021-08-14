/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mina.bo;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Mina Mimi
 */
@Entity
@Table(name = "sitemaps")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Sitemaps.findAll", query = "SELECT s FROM Sitemaps s")
    , @NamedQuery(name = "Sitemaps.findById", query = "SELECT s FROM Sitemaps s WHERE s.id = :id")
    , @NamedQuery(name = "Sitemaps.findByLink", query = "SELECT s FROM Sitemaps s WHERE s.link = :link")
    , @NamedQuery(name = "Sitemaps.findByStatus", query = "SELECT s FROM Sitemaps s WHERE s.status = :status")})
public class Sitemaps implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "link")
    private String link;
    @Column(name = "status")
    private Integer status;

    public Sitemaps() {
    }

    public Sitemaps(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
        if (!(object instanceof Sitemaps)) {
            return false;
        }
        Sitemaps other = (Sitemaps) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mina.bo.Sitemaps[ id=" + id + " ]";
    }
    
}
