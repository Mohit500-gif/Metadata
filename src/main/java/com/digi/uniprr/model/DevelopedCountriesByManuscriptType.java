package com.digi.uniprr.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name="developed_countries_by_manuscript_type")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DevelopedCountriesByManuscriptType {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="id")
    private Integer id;
    @Column(name="payment_countries_on_submission")
    private String paymentCountriesOnSubmission;

    @Column(name="payment_countries_on_publishing")
    private String paymentCountriesOnPublishing;

    @Column(name="journal_id")
    private Integer journalId;

    @Column(name="manuscript_type_id")
    private Integer manuscriptTypeId;

}
