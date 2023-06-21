import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    AdminBottomNavigationBarUnitTest::class,
    BottomNavigationBarUnitTest::class,
    LeaveRequestCardUnitTest::class,
)
class UnitTestSuite
