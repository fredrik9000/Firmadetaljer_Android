package com.github.fredrik9000.firmadetaljer_android.repository.data

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import companydb.CompanyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import sqldelight.companydb.CompanyDatabase

class CompanyDataSourceImpl(db: CompanyDatabase) : CompanyDataSource {

    private val queries = db.companyEntityQueries

    override fun getCompanies(): Flow<List<CompanyEntity>> {
        return queries.getCompaniesOrderedByName().asFlow().mapToList()
    }

    override suspend fun getCompanyByOrgNumber(orgNumber: Int): CompanyEntity? {
        return withContext(Dispatchers.IO) {
            queries.getCompanyByOrgNumber(orgNumber).executeAsOneOrNull()
        }
    }

    override suspend fun deleteCompanyByOrgNumber(orgNumber: Int) {
        withContext(Dispatchers.IO) {
            queries.deleteCompanyByOrgNumber(orgNumber)
        }
    }

    override suspend fun deleteAllCompanies() {
        withContext(Dispatchers.IO) {
            queries.deleteAllCompanies()
        }
    }

    override suspend fun insertCompany(companyEntity: CompanyEntity) {
        withContext(Dispatchers.IO) {
            queries.insertCompany(
                id = null, // null for id in order to auto increment
                organisasjonsnummer = companyEntity.organisasjonsnummer,
                navn = companyEntity.navn,
                stiftelsesdato = companyEntity.stiftelsesdato,
                registreringsdatoEnhetsregisteret = companyEntity.registreringsdatoEnhetsregisteret,
                oppstartsdato = companyEntity.oppstartsdato,
                datoEierskifte = companyEntity.datoEierskifte,
                organisasjonsform = companyEntity.organisasjonsform,
                hjemmeside = companyEntity.hjemmeside,
                registertIFrivillighetsregisteret = companyEntity.registertIFrivillighetsregisteret,
                registrertIMvaregisteret = companyEntity.registrertIMvaregisteret,
                registrertIForetaksregisteret = companyEntity.registrertIForetaksregisteret,
                registrertIStiftelsesregisteret = companyEntity.registrertIStiftelsesregisteret,
                antallAnsatte = companyEntity.antallAnsatte,
                sisteInnsendteAarsregnskap = companyEntity.sisteInnsendteAarsregnskap,
                konkurs = companyEntity.konkurs,
                underAvvikling = companyEntity.underAvvikling,
                underTvangsavviklingEllerTvangsopplosning = companyEntity.underTvangsavviklingEllerTvangsopplosning,
                overordnetEnhet = companyEntity.overordnetEnhet,
                institusjonellSektorkodeKode = companyEntity.institusjonellSektorkodeKode,
                institusjonellSektorkodeBeskrivelse = companyEntity.institusjonellSektorkodeBeskrivelse,
                naeringskode1Kode = companyEntity.naeringskode1Kode,
                naeringskode1Beskrivelse = companyEntity.naeringskode1Beskrivelse,
                naeringskode2Kode = companyEntity.naeringskode2Kode,
                naeringskode2Beskrivelse = companyEntity.naeringskode2Beskrivelse,
                naeringskode3Kode = companyEntity.naeringskode3Kode,
                naeringskode3Beskrivelse = companyEntity.naeringskode3Beskrivelse,
                postadresseAdresse = companyEntity.postadresseAdresse,
                postadressePostnummer = companyEntity.postadressePostnummer,
                postadressePoststed = companyEntity.postadressePoststed,
                postadresseKommunenummer = companyEntity.postadresseKommunenummer,
                postadresseKommune = companyEntity.postadresseKommune,
                postadresseLandkode = companyEntity.postadresseLandkode,
                postadresseLand = companyEntity.postadresseLand,
                forretningsadresseAdresse = companyEntity.forretningsadresseAdresse,
                forretningsadressePostnummer = companyEntity.forretningsadressePostnummer,
                forretningsadressePoststed = companyEntity.forretningsadressePoststed,
                forretningsadresseKommunenummer = companyEntity.forretningsadresseKommunenummer,
                forretningsadresseKommune = companyEntity.forretningsadresseKommune,
                forretningsadresseLandkode = companyEntity.forretningsadresseLandkode,
                forretningsadresseLand = companyEntity.forretningsadresseLand,
                beliggenhetsadresseAdresse = companyEntity.beliggenhetsadresseAdresse,
                beliggenhetsadressePostnummer = companyEntity.beliggenhetsadressePostnummer,
                beliggenhetsadressePoststed = companyEntity.beliggenhetsadressePoststed,
                beliggenhetsadresseKommunenummer = companyEntity.beliggenhetsadresseKommunenummer,
                beliggenhetsadresseKommune = companyEntity.beliggenhetsadresseKommune,
                beliggenhetsadresseLandkode = companyEntity.beliggenhetsadresseLandkode,
                beliggenhetsadresseLand = companyEntity.beliggenhetsadresseLand
            )
        }
    }
}