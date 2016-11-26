/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bremersee.common.domain.mongodb.entity;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bremersee.common.domain.entity.BaseEntity;
import org.bremersee.common.model.Base;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;

/**
 * @author Christian Bremer
 *
 */
public abstract class AbstractBaseMongo<PK extends Serializable> implements BaseEntity, Persistable<PK> {

    private static final long serialVersionUID = 1L;
    
    @Id
    private PK id;
    
    private Long created;
    
    private Long modified;
    
    protected Map<String, Object> extensions = new LinkedHashMap<>();

    /**
     * Default constructor. 
     */
    public AbstractBaseMongo() {
    }

    public AbstractBaseMongo(Base obj) {
        if (obj != null) {
            setCreated(obj.getCreated());
            setModified(obj.getModified());
            if (obj.getExtensions() != null) {
                getExtensions().putAll(obj.getExtensions());
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("rawtypes")
        AbstractBaseMongo other = (AbstractBaseMongo) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public boolean isNew() {
        return getId() == null;
    }

    @Override
    public PK getId() {
        return id;
    }

    public void setId(PK id) {
        this.id = id;
    }

    @Override
    public Long getCreated() {
        return created;
    }
    
    @Override
    public void setCreated(Long created) {
        this.created = created;
    }

    @Override
    public Long getModified() {
        return modified;
    }

    @Override
    public void setModified(Long modified) {
        this.modified = modified;
    }

    @Override
    public Map<String, Object> getExtensions() {
        if (extensions == null) {
            extensions = new LinkedHashMap<>();
        }
        return extensions;
    }
    
    @Override
    public void setExtensions(Map<String, Object> extensions) {
        if (extensions == null) {
            extensions = new LinkedHashMap<>();
        }
        this.extensions = extensions;
    }

}