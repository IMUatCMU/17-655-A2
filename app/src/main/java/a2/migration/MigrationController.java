package a2.migration;

import a2.common.ioc.AppBean;
import a2.common.ioc.BeanHolder;

/**
 * @author Weinan Qiu
 * @since 1.0.0
 */
public class MigrationController implements AppBean {

    private MigrationDao migrationDao;

    @Override
    public void afterInitialization() {
        this.migrationDao = (MigrationDao) BeanHolder.getBean(MigrationDao.class.getSimpleName());

        assert this.migrationDao != null;
    }

    public void performMigrationIfNecessary() {
        if (migrationDao.tableExists("eep_leaftech", "MIGRATION")) {
            if (migrationDao.isMigrationDone())
                return;
        } else {
            migrationDao.createMigrationTable();
        }

        DBMigration.performMigration();
        migrationDao.markMigrationAsDone();
    }
}
