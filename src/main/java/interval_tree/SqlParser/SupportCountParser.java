package interval_tree.SqlParser;

import interval_tree.FrequentPatternMining.SupportCount.TableCount;

/**
 * Created by Richard on 2018-03-04.
 */
public class SupportCountParser extends AbstractParser {
    private TableCount tableCount;

    public SupportCountParser(TableCount tableCount){
        super();
        this.tableCount = tableCount;
    }

    @Override
    public void before() {

    }

    @Override
    public void after() {

    }

    @Override
    protected void finiteInterval(String column, int start, int end) {
        tableCount.updateMinMax(getCurrentTable(), column, start, end);
    }

    @Override
    protected void equalsTo(String col, int point) {
        tableCount.updateMinMax(getCurrentTable(), col, point, point);
    }
}
