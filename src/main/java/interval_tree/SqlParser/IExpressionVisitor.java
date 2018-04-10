package interval_tree.SqlParser;

import net.sf.jsqlparser.expression.ExpressionVisitor;

public interface IExpressionVisitor extends ExpressionVisitor {
    void before();
    void after();
}
