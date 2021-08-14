/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mina.bo;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Mina Mimi
 */
@Entity
@Table(name = "manga_author")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MangaAuthor.findAll", query = "SELECT m FROM MangaAuthor m")
    , @NamedQuery(name = "MangaAuthor.findByMangaId", query = "SELECT m FROM MangaAuthor m WHERE m.mangaAuthorPK.mangaId = :mangaId")
    , @NamedQuery(name = "MangaAuthor.findByAuthorId", query = "SELECT m FROM MangaAuthor m WHERE m.mangaAuthorPK.authorId = :authorId")})
public class MangaAuthor implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected MangaAuthorPK mangaAuthorPK;

    public MangaAuthor() {
    }

    public MangaAuthor(MangaAuthorPK mangaAuthorPK) {
        this.mangaAuthorPK = mangaAuthorPK;
    }

    public MangaAuthor(long mangaId, int authorId) {
        this.mangaAuthorPK = new MangaAuthorPK(mangaId, authorId);
    }

    public MangaAuthorPK getMangaAuthorPK() {
        return mangaAuthorPK;
    }

    public void setMangaAuthorPK(MangaAuthorPK mangaAuthorPK) {
        this.mangaAuthorPK = mangaAuthorPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (mangaAuthorPK != null ? mangaAuthorPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MangaAuthor)) {
            return false;
        }
        MangaAuthor other = (MangaAuthor) object;
        if ((this.mangaAuthorPK == null && other.mangaAuthorPK != null) || (this.mangaAuthorPK != null && !this.mangaAuthorPK.equals(other.mangaAuthorPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mina.bo.MangaAuthor[ mangaAuthorPK=" + mangaAuthorPK + " ]";
    }
    
}
