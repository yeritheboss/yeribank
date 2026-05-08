package com.yeribank.core.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_log")
public class AuditLogJpaEntity {

  @Id
  private UUID id;

  @Column(name = "actor_user_id")
  private UUID actorUserId;

  @Column(nullable = false, length = 80)
  private String action;

  @Column(name = "resource_type", nullable = false, length = 80)
  private String resourceType;

  @Column(name = "resource_id", length = 120)
  private String resourceId;

  @Column(nullable = false, length = 30)
  private String status;

  @Column(name = "details_json")
  private String detailsJson;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getActorUserId() {
    return actorUserId;
  }

  public void setActorUserId(UUID actorUserId) {
    this.actorUserId = actorUserId;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String getResourceType() {
    return resourceType;
  }

  public void setResourceType(String resourceType) {
    this.resourceType = resourceType;
  }

  public String getResourceId() {
    return resourceId;
  }

  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getDetailsJson() {
    return detailsJson;
  }

  public void setDetailsJson(String detailsJson) {
    this.detailsJson = detailsJson;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
