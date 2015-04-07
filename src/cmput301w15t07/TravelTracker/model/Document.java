/*
 *   Copyright 2015 Kirby Banman,
 *                  Stuart Bildfell,
 *                  Elliot Colp,
 *                  Christian Ellinger,
 *                  Braedy Kuzma,
 *                  Ryan Thornhill
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package cmput301w15t07.TravelTracker.model;

import java.util.Date;
import java.util.UUID;

import cmput301w15t07.TravelTracker.serverinterface.Constants.Type;
import cmput301w15t07.TravelTracker.util.Observable;

/**
* Model object corresponding to storage documents.  Used to track cache/remote
* synchronization status.
* 
* @author kdbanman
*
*/
public abstract class Document extends Observable<Document> {

    // A new document may be synced, but dirty is a safer default.
    transient private boolean dirty = true;
    
    private Date lastChanged;
    
    private UUID docID;
    
    private Type type;
    
    /**
     * Package protected constructor, intended for use only by DataSource.
     * 
     * @param docID UUID document identifier
     */
    Document(UUID docID) {
        this.docID = docID;
        this.lastChanged = new Date();
    }
    
    /**
     * Private no-args constructor for GSON.
     */
    @SuppressWarnings("unused")
    private Document() {
        this.docID = UUID.randomUUID();
    }
    
    /**
     * @return Document/object identifier.
     */
    public UUID getUUID() {
        return docID;
    }
    
    /**
     * 
     * @return the last time the Document was changed by any setter.
     */
    public Date getLastChanged() {
        return lastChanged;
    }
    
    /** 
     * @return Whether or not the Document has been synced.
     */
    public boolean isDirty() {
        return dirty;
    }
    
    /**
     * Flag document as synchronized.
     */
    public void setClean() {
        dirty = false;
    }
    
    /**
     * Maintain caching state and update observers with a change.
     * To be called within *all* setters of Document subclasses.
     * 
     * @param self the Document that has changed.
     */
    public <E extends Document> void hasChanged(E self) {
        setDirty();
        setChangedDate();
        updateObservers(self);
    }
    
    /**
     * Flag document as modified and not yet synchronized.
     */
    private void setDirty() {
        dirty = true;
    }
    
    /**
     * Record an internal date of last change.
     */
    private void setChangedDate() {
        lastChanged = new Date();
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
    
    /**
     * @param sourceDoc the document whose attributes should be adopted
     * @return whether or not changes were made to attributes that equality or hashcode depend upon.
     */
    public boolean mergeAttributesFrom(Document sourceDoc) {
        if (!this.docID.equals(sourceDoc.getUUID()) || !this.type.equals(sourceDoc.getType()))
            return false;
        if (this.lastChanged.after(sourceDoc.getLastChanged()))
            return false;
        this.lastChanged = sourceDoc.getLastChanged();
        return mergeFrom(sourceDoc);
    }
    
    /**
     * @param sourceDoc the document whose attributes should be adopted
     * @return whether or not changes were made.
     */
    protected abstract boolean mergeFrom(Document sourceDoc);

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((docID == null) ? 0 : docID.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Document))
            return false;
        Document other = (Document) obj;
        if (docID == null) {
            if (other.docID != null)
                return false;
        } else if (!docID.equals(other.docID))
            return false;
        if (type != other.type)
            return false;
        return true;
    }
}
