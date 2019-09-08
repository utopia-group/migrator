// Generated from RewriteAntlrDsl.g4 by ANTLR 4.6

package migrator.rewrite.parser;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link RewriteAntlrDslParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface RewriteAntlrDslVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link RewriteAntlrDslParser#queryList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQueryList(RewriteAntlrDslParser.QueryListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code selectQuery}
	 * labeled alternative in {@link RewriteAntlrDslParser#query}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectQuery(RewriteAntlrDslParser.SelectQueryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code insertQuery}
	 * labeled alternative in {@link RewriteAntlrDslParser#query}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInsertQuery(RewriteAntlrDslParser.InsertQueryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code deleteQuery}
	 * labeled alternative in {@link RewriteAntlrDslParser#query}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeleteQuery(RewriteAntlrDslParser.DeleteQueryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code compoundDeleteQuery}
	 * labeled alternative in {@link RewriteAntlrDslParser#query}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompoundDeleteQuery(RewriteAntlrDslParser.CompoundDeleteQueryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code updateQuery}
	 * labeled alternative in {@link RewriteAntlrDslParser#query}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpdateQuery(RewriteAntlrDslParser.UpdateQueryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code compoundUpdateQuery}
	 * labeled alternative in {@link RewriteAntlrDslParser#query}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompoundUpdateQuery(RewriteAntlrDslParser.CompoundUpdateQueryContext ctx);
	/**
	 * Visit a parse tree produced by {@link RewriteAntlrDslParser#column}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColumn(RewriteAntlrDslParser.ColumnContext ctx);
	/**
	 * Visit a parse tree produced by {@link RewriteAntlrDslParser#table}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTable(RewriteAntlrDslParser.TableContext ctx);
	/**
	 * Visit a parse tree produced by {@link RewriteAntlrDslParser#join}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJoin(RewriteAntlrDslParser.JoinContext ctx);
	/**
	 * Visit a parse tree produced by {@link RewriteAntlrDslParser#set}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSet(RewriteAntlrDslParser.SetContext ctx);
	/**
	 * Visit a parse tree produced by {@link RewriteAntlrDslParser#predicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPredicate(RewriteAntlrDslParser.PredicateContext ctx);
	/**
	 * Visit a parse tree produced by {@link RewriteAntlrDslParser#predOr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPredOr(RewriteAntlrDslParser.PredOrContext ctx);
	/**
	 * Visit a parse tree produced by {@link RewriteAntlrDslParser#predAnd}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPredAnd(RewriteAntlrDslParser.PredAndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code predPositive}
	 * labeled alternative in {@link RewriteAntlrDslParser#predNot}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPredPositive(RewriteAntlrDslParser.PredPositiveContext ctx);
	/**
	 * Visit a parse tree produced by the {@code predNegate}
	 * labeled alternative in {@link RewriteAntlrDslParser#predNot}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPredNegate(RewriteAntlrDslParser.PredNegateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code predParen}
	 * labeled alternative in {@link RewriteAntlrDslParser#predTop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPredParen(RewriteAntlrDslParser.PredParenContext ctx);
	/**
	 * Visit a parse tree produced by the {@code predIneq}
	 * labeled alternative in {@link RewriteAntlrDslParser#predTop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPredIneq(RewriteAntlrDslParser.PredIneqContext ctx);
	/**
	 * Visit a parse tree produced by the {@code predEq}
	 * labeled alternative in {@link RewriteAntlrDslParser#predTop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPredEq(RewriteAntlrDslParser.PredEqContext ctx);
	/**
	 * Visit a parse tree produced by the {@code predIn}
	 * labeled alternative in {@link RewriteAntlrDslParser#predTop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPredIn(RewriteAntlrDslParser.PredInContext ctx);
	/**
	 * Visit a parse tree produced by the {@code valueParameter}
	 * labeled alternative in {@link RewriteAntlrDslParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValueParameter(RewriteAntlrDslParser.ValueParameterContext ctx);
	/**
	 * Visit a parse tree produced by the {@code valueIdentifier}
	 * labeled alternative in {@link RewriteAntlrDslParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValueIdentifier(RewriteAntlrDslParser.ValueIdentifierContext ctx);
	/**
	 * Visit a parse tree produced by the {@code valueNumber}
	 * labeled alternative in {@link RewriteAntlrDslParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValueNumber(RewriteAntlrDslParser.ValueNumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code valueSubquery}
	 * labeled alternative in {@link RewriteAntlrDslParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValueSubquery(RewriteAntlrDslParser.ValueSubqueryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code valueFresh}
	 * labeled alternative in {@link RewriteAntlrDslParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValueFresh(RewriteAntlrDslParser.ValueFreshContext ctx);
	/**
	 * Visit a parse tree produced by {@link RewriteAntlrDslParser#subquery}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubquery(RewriteAntlrDslParser.SubqueryContext ctx);
	/**
	 * Visit a parse tree produced by {@link RewriteAntlrDslParser#schema}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSchema(RewriteAntlrDslParser.SchemaContext ctx);
	/**
	 * Visit a parse tree produced by {@link RewriteAntlrDslParser#tableDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTableDef(RewriteAntlrDslParser.TableDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code columnDef}
	 * labeled alternative in {@link RewriteAntlrDslParser#columnStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColumnDef(RewriteAntlrDslParser.ColumnDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code pkDef}
	 * labeled alternative in {@link RewriteAntlrDslParser#columnStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPkDef(RewriteAntlrDslParser.PkDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fkDef}
	 * labeled alternative in {@link RewriteAntlrDslParser#columnStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFkDef(RewriteAntlrDslParser.FkDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link RewriteAntlrDslParser#tuple}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTuple(RewriteAntlrDslParser.TupleContext ctx);
	/**
	 * Visit a parse tree produced by {@link RewriteAntlrDslParser#pair}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPair(RewriteAntlrDslParser.PairContext ctx);
	/**
	 * Visit a parse tree produced by {@link RewriteAntlrDslParser#tupleList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTupleList(RewriteAntlrDslParser.TupleListContext ctx);
	/**
	 * Visit a parse tree produced by {@link RewriteAntlrDslParser#mapping}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMapping(RewriteAntlrDslParser.MappingContext ctx);
	/**
	 * Visit a parse tree produced by {@link RewriteAntlrDslParser#tableMapping}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTableMapping(RewriteAntlrDslParser.TableMappingContext ctx);
	/**
	 * Visit a parse tree produced by {@link RewriteAntlrDslParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(RewriteAntlrDslParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link RewriteAntlrDslParser#signature}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSignature(RewriteAntlrDslParser.SignatureContext ctx);
	/**
	 * Visit a parse tree produced by {@link RewriteAntlrDslParser#method}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethod(RewriteAntlrDslParser.MethodContext ctx);
	/**
	 * Visit a parse tree produced by {@link RewriteAntlrDslParser#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter(RewriteAntlrDslParser.ParameterContext ctx);
}