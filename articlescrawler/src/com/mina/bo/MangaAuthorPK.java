/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mina.bo;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Mina Mimi
 */
@Embeddable
public class MangaAuthorPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "manga_id")
    private long mangaId;
    @Basic(optional = false)
    @Column(name = "author_id")
    private int authorId;

    public MangaAuthorPK() {
    }

    public MangaAuthorPK(long mangaId, int authorId) {
        this.mangaId = mangaId;
        this.authorId = authorId;
    }

    public long getMangaId() {
        return mangaId;
    }

    public void setMangaId(long mangaId) {
        this.mangaId = mangaId;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) mangaId;
        hash += (int) authorId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MangaAuthorPK)) {
            return false;
        }
        MangaAuthorPK other = (MangaAuthorPK) object;
        if (this.mangaId != other.mangaId) {
            return false;
        }
        if (this.authorId != other.authorId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mina.bo.MangaAuthorPK[ mangaId=" + mangaId + ", authorId=" + authorId + " ]";
    }
    
}
