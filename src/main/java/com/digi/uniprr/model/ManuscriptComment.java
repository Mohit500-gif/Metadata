package com.digi.uniprr.model;

import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "manuscript_comment")
@Getter
@Setter
public class ManuscriptComment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(name = "manuscript_id")
	private Integer manuscriptId;

	@Column(name = "author_email")
	private String authorEmail;

	@Column(name = "task_id")
	private Integer taskId;

	@Column(name = "task_name")
	private String taskName;

	@Column(name = "comment_data")
	private String comment;
	
	@Column(name = "uniprr_task")
	private String uniprrTask;
	
	
}
