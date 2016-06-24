package net.amg.jira.plugins.jrmp.services

import com.atlassian.query.QueryImpl
import net.amg.jira.plugins.jrmp.services.model.DateModel
import net.amg.jira.plugins.jrmp.services.model.ProjectOrFilter
import net.amg.jira.plugins.jrmp.services.model.RiskIssues

/**
 * Created by yahro01 on 7/6/15.
 */
class MatrixGeneratorImplTest extends spock.lang.Specification {

    def MatrixGenerator generator
    def JRMPSearchServiceImpl searchService = Mock(JRMPSearchServiceImpl.class)
    def RiskIssuesFinder issuesFinder = Mock(RiskIssuesFinder.class)
    def RenderTemplateService templateService = Mock(RenderTemplateService.class)

    def setup() {
        generator = new MatrixGeneratorImpl()
        generator.jrmpSearchService = searchService
        generator.riskIssuesFinder = issuesFinder
        generator.renderTemplate = templateService
    }

    def "should generate simple matrix"() {

        def query = new QueryImpl()
        def filter = new ProjectOrFilter(query: query)
        def riskIssues = new RiskIssues()
        when:
        def result = generator.generateMatrix(filter, "matrix title", "matrix template", DateModel.SIX_MONTHS_AGO)
        then:
        1 * searchService.getAllQualifiedIssues(query, DateModel.SIX_MONTHS_AGO) >> { [] }
        1 * issuesFinder.fillAllFields([], query, DateModel.SIX_MONTHS_AGO) >> { riskIssues }
        1 * templateService.renderTemplate(filter,"matrix title", "matrix template", riskIssues) >> { "someting" }
        result == "someting"
    }
}
