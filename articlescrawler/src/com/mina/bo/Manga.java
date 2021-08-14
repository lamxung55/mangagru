/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mina.bo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Mina Mimi
 */
@Entity
@Table(name = "manga")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Manga.findAll", query = "SELECT m FROM Manga m")
    , @NamedQuery(name = "Manga.findById", query = "SELECT m FROM Manga m WHERE m.id = :id")
    , @NamedQuery(name = "Manga.findBySource", query = "SELECT m FROM Manga m WHERE m.source = :source")
    , @NamedQuery(name = "Manga.findByName", query = "SELECT m FROM Manga m WHERE m.name = :name")
    , @NamedQuery(name = "Manga.findByNameother", query = "SELECT m FROM Manga m WHERE m.nameother = :nameother")
    , @NamedQuery(name = "Manga.findBySlug", query = "SELECT m FROM Manga m WHERE m.slug = :slug")
    , @NamedQuery(name = "Manga.findByStatus", query = "SELECT m FROM Manga m WHERE m.status = :status")
    , @NamedQuery(name = "Manga.findByImage", query = "SELECT m FROM Manga m WHERE m.image = :image")
    , @NamedQuery(name = "Manga.findByViews", query = "SELECT m FROM Manga m WHERE m.views = :views")
    , @NamedQuery(name = "Manga.findByKeywords", query = "SELECT m FROM Manga m WHERE m.keywords = :keywords")
    , @NamedQuery(name = "Manga.findByAuthor", query = "SELECT m FROM Manga m WHERE m.author = :author")
    , @NamedQuery(name = "Manga.findByScore", query = "SELECT m FROM Manga m WHERE m.score = :score")
    , @NamedQuery(name = "Manga.findByTotalRate", query = "SELECT m FROM Manga m WHERE m.totalRate = :totalRate")
    , @NamedQuery(name = "Manga.findByCreatedAt", query = "SELECT m FROM Manga m WHERE m.createdAt = :createdAt")
    , @NamedQuery(name = "Manga.findByUpdatedAt", query = "SELECT m FROM Manga m WHERE m.updatedAt = :updatedAt")
    , @NamedQuery(name = "Manga.findByLastUpdate", query = "SELECT m FROM Manga m WHERE m.lastUpdate = :lastUpdate")
    , @NamedQuery(name = "Manga.findByChapter", query = "SELECT m FROM Manga m WHERE m.chapter = :chapter")
    , @NamedQuery(name = "Manga.findByActive", query = "SELECT m FROM Manga m WHERE m.active = :active")
    , @NamedQuery(name = "Manga.findByHot", query = "SELECT m FROM Manga m WHERE m.hot = :hot")})
public class Manga implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "source")
    private String source;
    @Column(name = "name")
    private String name;
    @Column(name = "nameother")
    private String nameother;
    @Column(name = "slug")
    private String slug;
    @Lob
    @Column(name = "content")
    private String content;
    @Column(name = "status")
    private String status;
    @Column(name = "image")
    private String image;
    @Column(name = "views")
    private long views;
    @Column(name = "keywords")
    private String keywords;
    @Column(name = "author")
    private String author;
    @Lob
    @Column(name = "genres")
    private String genres;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "score")
    private Float score;
    @Column(name = "total_rate")
    private Integer totalRate;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Column(name = "last_update")
    private String lastUpdate;
    @Column(name = "chapter")
    private Integer chapter;
    @Column(name = "active")
    private Integer active;
    @Column(name = "hot")
    private Short hot;
    @Lob
    @Column(name = "latest_chapter")
    private String latestChapter;

    public Manga() {
    }

    public Manga(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameother() {
        return nameother;
    }

    public void setNameother(String nameother) {
        this.nameother = nameother;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public Integer getTotalRate() {
        return totalRate;
    }

    public void setTotalRate(Integer totalRate) {
        this.totalRate = totalRate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Integer getChapter() {
        return chapter;
    }

    public void setChapter(Integer chapter) {
        this.chapter = chapter;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Short getHot() {
        return hot;
    }

    public void setHot(Short hot) {
        this.hot = hot;
    }

    public String getLatestChapter() {
        return latestChapter;
    }

    public void setLatestChapter(String latestChapter) {
        this.latestChapter = latestChapter;
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
        if (!(object instanceof Manga)) {
            return false;
        }
        Manga other = (Manga) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mina.bo.Manga[ id=" + id + " ]";
    }
    
}
