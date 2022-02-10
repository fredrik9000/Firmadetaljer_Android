package com.github.fredrik9000.firmadetaljer_android.repository.data

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import companydb.CompanyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import sqldelight.companydb.CompanyDatabase

class CompanyDataSourceImpl(db: CompanyDatabase): CompanyDataSource {

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
            queries.insertCompany(null, // null for id in order to auto increment
                companyEntity.organisasjonsnummer,
                companyEntity.navn,
                companyEntity.stiftelsesdato,
                companyEntity.registreringsdatoEnhetsregisteret,
                companyEntity.oppstartsdato,
                companyEntity.datoEierskifte,
                companyEntity.organisasjonsform,
                companyEntity.hjemmeside,
                companyEntity.registertIFrivillighetsregisteret,
                companyEntity.registrertIMvaregisteret,
                companyEntity.registrertIForetaksregisteret,
                companyEntity.registrertIStiftelsesregisteret,
                companyEntity.antallAnsatte,
                companyEntity.sisteInnsendteAarsregnskap,
                companyEntity.konkurs,
                companyEntity.underAvvikling,
                companyEntity.underTvangsavviklingEllerTvangsopplosning,
                companyEntity.overordnetEnhet,
                companyEntity.institusjonellSektorkodeKode,
                companyEntity.institusjonellSektorkodeBeskrivelse,
                companyEntity.naeringskode1Kode,
                companyEntity.naeringskode1Beskrivelse,
                companyEntity.naeringskode2Kode,
                companyEntity.naeringskode2Beskrivelse,
                companyEntity.naeringskode3Kode,
                companyEntity.naeringskode3Beskrivelse,
                companyEntity.postadresseAdresse,
                companyEntity.postadressePostnummer,
                companyEntity.postadressePoststed,
                companyEntity.postadresseKommunenummer,
                companyEntity.postadresseKommune,
                companyEntity.postadresseLandkode,
                companyEntity.postadresseLand,
                companyEntity.forretningsadresseAdresse,
                companyEntity.forretningsadressePostnummer,
                companyEntity.forretningsadressePoststed,
                companyEntity.forretningsadresseKommunenummer,
                companyEntity.forretningsadresseKommune,
                companyEntity.forretningsadresseLandkode,
                companyEntity.forretningsadresseLand,
                companyEntity.beliggenhetsadresseAdresse,
                companyEntity.beliggenhetsadressePostnummer,
                companyEntity.beliggenhetsadressePoststed,
                companyEntity.beliggenhetsadresseKommunenummer,
                companyEntity.beliggenhetsadresseKommune,
                companyEntity.beliggenhetsadresseLandkode,
                companyEntity.beliggenhetsadresseLand
            )
        }
    }
}