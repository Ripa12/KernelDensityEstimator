package interval_tree.SqlParser;

import interval_tree.DataStructure.IntervalTree;
import interval_tree.FrequentPatternMining.SupportCount;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.List;
import java.util.Map;

/**
 * Created by Richard on 2018-03-04.
 */
public class SupportCountParser extends AbstractParser {
    private SupportCount supportCount;

    public SupportCountParser(SupportCount supportCount){
        super();
        this.supportCount = supportCount;
    }

    @Override
    public void before() {

    }

    @Override
    public void after() {
        supportCount.updateTotalSupport(); // ToDo: Find a more clever way
    }

    @Override
    protected void finiteInterval(String column, int start, int end) {
        supportCount.get(column)[0]++;
        supportCount.get(column)[1] = Math.min(supportCount.get(column)[1], start);
        supportCount.get(column)[2] = Math.max(supportCount.get(column)[2], end);
    }


    @Override
    protected void equalsTo(String col, int point) {
        supportCount.get(col)[0]++;
        supportCount.get(col)[1] = Math.min(supportCount.get(col)[1], point);
        supportCount.get(col)[2] = Math.max(supportCount.get(col)[2], point);
    }
}
