package interval_tree.SqlParser;

import interval_tree.DataStructure.IntervalTree;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.Map;

/**
 * Created by Richard on 2018-03-04.
 */
public class GenericExpressionVisitor extends AbstractParser {

    private Map<String, IntervalTree> intervalTrees;

    public GenericExpressionVisitor(Map<String, IntervalTree> trees){
        super();
        intervalTrees = trees;
    }

    @Override
    public void before() {

    }

    @Override
    public void after() {

    }

    @Override
    void finiteInterval(String column, int start, int end) {
        intervalTrees.get(column).insert(new IntervalTree.Interval(start, end));
    }

    @Override
    void equalsTo(String col, int point) {
        intervalTrees.get(col).insert(new IntervalTree.Point(point));
    }
}
