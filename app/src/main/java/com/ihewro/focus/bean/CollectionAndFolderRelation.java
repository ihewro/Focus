package com.ihewro.focus.bean;

import org.litepal.crud.LitePalSupport;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/06/27
 *     desc   : 收藏与收藏分类是多对多的关系
 *     version: 1.0
 * </pre>
 */
public class CollectionAndFolderRelation extends LitePalSupport {

    private int id;
    private int collectionId;
    private int collectionFolderId;

    public CollectionAndFolderRelation() {
    }

    public CollectionAndFolderRelation(int collectionId, int collectionFolderId) {
        this.collectionId = collectionId;
        this.collectionFolderId = collectionFolderId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(int collectionId) {
        this.collectionId = collectionId;
    }

    public int getCollectionFolderId() {
        return collectionFolderId;
    }

    public void setCollectionFolderId(int collectionFolderId) {
        this.collectionFolderId = collectionFolderId;
    }
}
