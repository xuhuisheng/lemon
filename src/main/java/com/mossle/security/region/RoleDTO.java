package com.mossle.security.region;

public class RoleDTO {
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Object o) {
        if (o instanceof RoleDTO) {
            RoleDTO roleDto = (RoleDTO) o;

            return roleDto.getId().equals(this.id);
        }

        return false;
    }

    public int hashCode() {
        return id.hashCode();
    }

    public String toString() {
        return "RoleDTO(" + id + "," + name + ")";
    }
}
