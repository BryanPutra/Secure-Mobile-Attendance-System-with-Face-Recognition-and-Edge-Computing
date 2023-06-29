import com.example.Thesis_Project.CompanyQuotasRowUITest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    ButtonHalfWidthUITest::class,
    ButtonMaxWidthUITest::class,
    CalendarStatusUITest::class,
    CompanyQuotasRowUITest::class,
//    CorrectionRequestCardUITest::class,
//    LeaveRequestCardUITest::class
)
class UITestSuite