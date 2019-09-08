// Generated from RewriteAntlrDsl.g4 by ANTLR 4.6

package migrator.rewrite.parser;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class RewriteAntlrDslParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.6", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, IN=9, 
		AND=10, OR=11, NOT=12, SELECT=13, UPDATE=14, INSERT=15, DELETE=16, AS=17, 
		ON=18, SET=19, FROM=20, INTO=21, JOIN=22, WHERE=23, VALUES=24, CREATE=25, 
		TABLE=26, PRIMARY=27, FOREIGN=28, KEY=29, REFERENCES=30, QUERY=31, FRESH=32, 
		EQ=33, INEQ=34, ID=35, NUM=36, WS=37;
	public static final int
		RULE_queryList = 0, RULE_query = 1, RULE_column = 2, RULE_table = 3, RULE_join = 4, 
		RULE_set = 5, RULE_predicate = 6, RULE_predOr = 7, RULE_predAnd = 8, RULE_predNot = 9, 
		RULE_predTop = 10, RULE_value = 11, RULE_subquery = 12, RULE_schema = 13, 
		RULE_tableDef = 14, RULE_columnStmt = 15, RULE_tuple = 16, RULE_pair = 17, 
		RULE_tupleList = 18, RULE_mapping = 19, RULE_tableMapping = 20, RULE_program = 21, 
		RULE_signature = 22, RULE_method = 23, RULE_parameter = 24;
	public static final String[] ruleNames = {
		"queryList", "query", "column", "table", "join", "set", "predicate", "predOr", 
		"predAnd", "predNot", "predTop", "value", "subquery", "schema", "tableDef", 
		"columnStmt", "tuple", "pair", "tupleList", "mapping", "tableMapping", 
		"program", "signature", "method", "parameter"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "','", "';'", "'('", "')'", "'?'", "'->'", "'{'", "'}'", null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, "'='"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, "IN", "AND", "OR", 
		"NOT", "SELECT", "UPDATE", "INSERT", "DELETE", "AS", "ON", "SET", "FROM", 
		"INTO", "JOIN", "WHERE", "VALUES", "CREATE", "TABLE", "PRIMARY", "FOREIGN", 
		"KEY", "REFERENCES", "QUERY", "FRESH", "EQ", "INEQ", "ID", "NUM", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "RewriteAntlrDsl.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public RewriteAntlrDslParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class QueryListContext extends ParserRuleContext {
		public List<QueryContext> query() {
			return getRuleContexts(QueryContext.class);
		}
		public QueryContext query(int i) {
			return getRuleContext(QueryContext.class,i);
		}
		public QueryListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_queryList; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitQueryList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QueryListContext queryList() throws RecognitionException {
		QueryListContext _localctx = new QueryListContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_queryList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(53);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << SELECT) | (1L << UPDATE) | (1L << INSERT) | (1L << DELETE))) != 0)) {
				{
				{
				setState(50);
				query();
				}
				}
				setState(55);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class QueryContext extends ParserRuleContext {
		public QueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query; }
	 
		public QueryContext() { }
		public void copyFrom(QueryContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class DeleteQueryContext extends QueryContext {
		public TerminalNode DELETE() { return getToken(RewriteAntlrDslParser.DELETE, 0); }
		public TerminalNode FROM() { return getToken(RewriteAntlrDslParser.FROM, 0); }
		public TableContext table() {
			return getRuleContext(TableContext.class,0);
		}
		public TerminalNode WHERE() { return getToken(RewriteAntlrDslParser.WHERE, 0); }
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public DeleteQueryContext(QueryContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitDeleteQuery(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CompoundDeleteQueryContext extends QueryContext {
		public TerminalNode DELETE() { return getToken(RewriteAntlrDslParser.DELETE, 0); }
		public List<TableContext> table() {
			return getRuleContexts(TableContext.class);
		}
		public TableContext table(int i) {
			return getRuleContext(TableContext.class,i);
		}
		public TerminalNode FROM() { return getToken(RewriteAntlrDslParser.FROM, 0); }
		public List<JoinContext> join() {
			return getRuleContexts(JoinContext.class);
		}
		public JoinContext join(int i) {
			return getRuleContext(JoinContext.class,i);
		}
		public TerminalNode WHERE() { return getToken(RewriteAntlrDslParser.WHERE, 0); }
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public CompoundDeleteQueryContext(QueryContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitCompoundDeleteQuery(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class UpdateQueryContext extends QueryContext {
		public TerminalNode UPDATE() { return getToken(RewriteAntlrDslParser.UPDATE, 0); }
		public TableContext table() {
			return getRuleContext(TableContext.class,0);
		}
		public TerminalNode SET() { return getToken(RewriteAntlrDslParser.SET, 0); }
		public List<SetContext> set() {
			return getRuleContexts(SetContext.class);
		}
		public SetContext set(int i) {
			return getRuleContext(SetContext.class,i);
		}
		public TerminalNode WHERE() { return getToken(RewriteAntlrDslParser.WHERE, 0); }
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public UpdateQueryContext(QueryContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitUpdateQuery(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CompoundUpdateQueryContext extends QueryContext {
		public TerminalNode UPDATE() { return getToken(RewriteAntlrDslParser.UPDATE, 0); }
		public TableContext table() {
			return getRuleContext(TableContext.class,0);
		}
		public TerminalNode SET() { return getToken(RewriteAntlrDslParser.SET, 0); }
		public List<SetContext> set() {
			return getRuleContexts(SetContext.class);
		}
		public SetContext set(int i) {
			return getRuleContext(SetContext.class,i);
		}
		public List<JoinContext> join() {
			return getRuleContexts(JoinContext.class);
		}
		public JoinContext join(int i) {
			return getRuleContext(JoinContext.class,i);
		}
		public TerminalNode WHERE() { return getToken(RewriteAntlrDslParser.WHERE, 0); }
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public CompoundUpdateQueryContext(QueryContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitCompoundUpdateQuery(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class InsertQueryContext extends QueryContext {
		public TerminalNode INSERT() { return getToken(RewriteAntlrDslParser.INSERT, 0); }
		public TerminalNode INTO() { return getToken(RewriteAntlrDslParser.INTO, 0); }
		public TableContext table() {
			return getRuleContext(TableContext.class,0);
		}
		public List<ColumnContext> column() {
			return getRuleContexts(ColumnContext.class);
		}
		public ColumnContext column(int i) {
			return getRuleContext(ColumnContext.class,i);
		}
		public TerminalNode VALUES() { return getToken(RewriteAntlrDslParser.VALUES, 0); }
		public List<ValueContext> value() {
			return getRuleContexts(ValueContext.class);
		}
		public ValueContext value(int i) {
			return getRuleContext(ValueContext.class,i);
		}
		public InsertQueryContext(QueryContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitInsertQuery(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SelectQueryContext extends QueryContext {
		public TerminalNode SELECT() { return getToken(RewriteAntlrDslParser.SELECT, 0); }
		public List<ColumnContext> column() {
			return getRuleContexts(ColumnContext.class);
		}
		public ColumnContext column(int i) {
			return getRuleContext(ColumnContext.class,i);
		}
		public TerminalNode FROM() { return getToken(RewriteAntlrDslParser.FROM, 0); }
		public TableContext table() {
			return getRuleContext(TableContext.class,0);
		}
		public List<JoinContext> join() {
			return getRuleContexts(JoinContext.class);
		}
		public JoinContext join(int i) {
			return getRuleContext(JoinContext.class,i);
		}
		public TerminalNode WHERE() { return getToken(RewriteAntlrDslParser.WHERE, 0); }
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public SelectQueryContext(QueryContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitSelectQuery(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QueryContext query() throws RecognitionException {
		QueryContext _localctx = new QueryContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_query);
		int _la;
		try {
			setState(176);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				_localctx = new SelectQueryContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(56);
				match(SELECT);
				setState(57);
				column();
				setState(62);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(58);
					match(T__0);
					setState(59);
					column();
					}
					}
					setState(64);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(65);
				match(FROM);
				setState(66);
				table();
				setState(70);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==JOIN) {
					{
					{
					setState(67);
					join();
					}
					}
					setState(72);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(75);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WHERE) {
					{
					setState(73);
					match(WHERE);
					setState(74);
					predicate();
					}
				}

				setState(77);
				match(T__1);
				}
				break;
			case 2:
				_localctx = new InsertQueryContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(79);
				match(INSERT);
				setState(80);
				match(INTO);
				setState(81);
				table();
				setState(82);
				match(T__2);
				setState(83);
				column();
				setState(88);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(84);
					match(T__0);
					setState(85);
					column();
					}
					}
					setState(90);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(91);
				match(T__3);
				setState(92);
				match(VALUES);
				setState(93);
				match(T__2);
				setState(94);
				value();
				setState(99);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(95);
					match(T__0);
					setState(96);
					value();
					}
					}
					setState(101);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(102);
				match(T__3);
				setState(103);
				match(T__1);
				}
				break;
			case 3:
				_localctx = new DeleteQueryContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(105);
				match(DELETE);
				setState(106);
				match(FROM);
				setState(107);
				table();
				setState(110);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WHERE) {
					{
					setState(108);
					match(WHERE);
					setState(109);
					predicate();
					}
				}

				setState(112);
				match(T__1);
				}
				break;
			case 4:
				_localctx = new CompoundDeleteQueryContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(114);
				match(DELETE);
				setState(115);
				table();
				setState(120);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(116);
					match(T__0);
					setState(117);
					table();
					}
					}
					setState(122);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(123);
				match(FROM);
				setState(124);
				table();
				setState(128);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==JOIN) {
					{
					{
					setState(125);
					join();
					}
					}
					setState(130);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(133);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WHERE) {
					{
					setState(131);
					match(WHERE);
					setState(132);
					predicate();
					}
				}

				setState(135);
				match(T__1);
				}
				break;
			case 5:
				_localctx = new UpdateQueryContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(137);
				match(UPDATE);
				setState(138);
				table();
				setState(139);
				match(SET);
				setState(140);
				set();
				setState(145);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(141);
					match(T__0);
					setState(142);
					set();
					}
					}
					setState(147);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(150);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WHERE) {
					{
					setState(148);
					match(WHERE);
					setState(149);
					predicate();
					}
				}

				setState(152);
				match(T__1);
				}
				break;
			case 6:
				_localctx = new CompoundUpdateQueryContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(154);
				match(UPDATE);
				setState(155);
				table();
				setState(157); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(156);
					join();
					}
					}
					setState(159); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==JOIN );
				setState(161);
				match(SET);
				setState(162);
				set();
				setState(167);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(163);
					match(T__0);
					setState(164);
					set();
					}
					}
					setState(169);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(172);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WHERE) {
					{
					setState(170);
					match(WHERE);
					setState(171);
					predicate();
					}
				}

				setState(174);
				match(T__1);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ColumnContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(RewriteAntlrDslParser.ID, 0); }
		public ColumnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_column; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitColumn(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ColumnContext column() throws RecognitionException {
		ColumnContext _localctx = new ColumnContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_column);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(178);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TableContext extends ParserRuleContext {
		public Token name;
		public Token reference;
		public List<TerminalNode> ID() { return getTokens(RewriteAntlrDslParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(RewriteAntlrDslParser.ID, i);
		}
		public TerminalNode AS() { return getToken(RewriteAntlrDslParser.AS, 0); }
		public TableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitTable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TableContext table() throws RecognitionException {
		TableContext _localctx = new TableContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_table);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(180);
			((TableContext)_localctx).name = match(ID);
			setState(183);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS) {
				{
				setState(181);
				match(AS);
				setState(182);
				((TableContext)_localctx).reference = match(ID);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class JoinContext extends ParserRuleContext {
		public TerminalNode JOIN() { return getToken(RewriteAntlrDslParser.JOIN, 0); }
		public TableContext table() {
			return getRuleContext(TableContext.class,0);
		}
		public TerminalNode ON() { return getToken(RewriteAntlrDslParser.ON, 0); }
		public List<ColumnContext> column() {
			return getRuleContexts(ColumnContext.class);
		}
		public ColumnContext column(int i) {
			return getRuleContext(ColumnContext.class,i);
		}
		public TerminalNode EQ() { return getToken(RewriteAntlrDslParser.EQ, 0); }
		public JoinContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_join; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitJoin(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JoinContext join() throws RecognitionException {
		JoinContext _localctx = new JoinContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_join);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(185);
			match(JOIN);
			setState(186);
			table();
			setState(187);
			match(ON);
			setState(188);
			column();
			setState(189);
			match(EQ);
			setState(190);
			column();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SetContext extends ParserRuleContext {
		public ColumnContext column() {
			return getRuleContext(ColumnContext.class,0);
		}
		public TerminalNode EQ() { return getToken(RewriteAntlrDslParser.EQ, 0); }
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public SetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_set; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitSet(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SetContext set() throws RecognitionException {
		SetContext _localctx = new SetContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_set);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(192);
			column();
			setState(193);
			match(EQ);
			setState(194);
			value();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PredicateContext extends ParserRuleContext {
		public PredOrContext predOr() {
			return getRuleContext(PredOrContext.class,0);
		}
		public PredicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicate; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitPredicate(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PredicateContext predicate() throws RecognitionException {
		PredicateContext _localctx = new PredicateContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(196);
			predOr();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PredOrContext extends ParserRuleContext {
		public List<PredAndContext> predAnd() {
			return getRuleContexts(PredAndContext.class);
		}
		public PredAndContext predAnd(int i) {
			return getRuleContext(PredAndContext.class,i);
		}
		public List<TerminalNode> OR() { return getTokens(RewriteAntlrDslParser.OR); }
		public TerminalNode OR(int i) {
			return getToken(RewriteAntlrDslParser.OR, i);
		}
		public PredOrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predOr; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitPredOr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PredOrContext predOr() throws RecognitionException {
		PredOrContext _localctx = new PredOrContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_predOr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(198);
			predAnd();
			setState(203);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OR) {
				{
				{
				setState(199);
				match(OR);
				setState(200);
				predAnd();
				}
				}
				setState(205);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PredAndContext extends ParserRuleContext {
		public List<PredNotContext> predNot() {
			return getRuleContexts(PredNotContext.class);
		}
		public PredNotContext predNot(int i) {
			return getRuleContext(PredNotContext.class,i);
		}
		public List<TerminalNode> AND() { return getTokens(RewriteAntlrDslParser.AND); }
		public TerminalNode AND(int i) {
			return getToken(RewriteAntlrDslParser.AND, i);
		}
		public PredAndContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predAnd; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitPredAnd(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PredAndContext predAnd() throws RecognitionException {
		PredAndContext _localctx = new PredAndContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_predAnd);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(206);
			predNot();
			setState(211);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AND) {
				{
				{
				setState(207);
				match(AND);
				setState(208);
				predNot();
				}
				}
				setState(213);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PredNotContext extends ParserRuleContext {
		public PredNotContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predNot; }
	 
		public PredNotContext() { }
		public void copyFrom(PredNotContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class PredNegateContext extends PredNotContext {
		public TerminalNode NOT() { return getToken(RewriteAntlrDslParser.NOT, 0); }
		public PredNotContext predNot() {
			return getRuleContext(PredNotContext.class,0);
		}
		public PredNegateContext(PredNotContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitPredNegate(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class PredPositiveContext extends PredNotContext {
		public PredTopContext predTop() {
			return getRuleContext(PredTopContext.class,0);
		}
		public PredPositiveContext(PredNotContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitPredPositive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PredNotContext predNot() throws RecognitionException {
		PredNotContext _localctx = new PredNotContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_predNot);
		try {
			setState(217);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				_localctx = new PredPositiveContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(214);
				predTop();
				}
				break;
			case 2:
				_localctx = new PredNegateContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(215);
				match(NOT);
				setState(216);
				predNot();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PredTopContext extends ParserRuleContext {
		public PredTopContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predTop; }
	 
		public PredTopContext() { }
		public void copyFrom(PredTopContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class PredInContext extends PredTopContext {
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public TerminalNode IN() { return getToken(RewriteAntlrDslParser.IN, 0); }
		public SubqueryContext subquery() {
			return getRuleContext(SubqueryContext.class,0);
		}
		public PredInContext(PredTopContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitPredIn(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class PredIneqContext extends PredTopContext {
		public List<ValueContext> value() {
			return getRuleContexts(ValueContext.class);
		}
		public ValueContext value(int i) {
			return getRuleContext(ValueContext.class,i);
		}
		public TerminalNode INEQ() { return getToken(RewriteAntlrDslParser.INEQ, 0); }
		public PredIneqContext(PredTopContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitPredIneq(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class PredEqContext extends PredTopContext {
		public List<ValueContext> value() {
			return getRuleContexts(ValueContext.class);
		}
		public ValueContext value(int i) {
			return getRuleContext(ValueContext.class,i);
		}
		public TerminalNode EQ() { return getToken(RewriteAntlrDslParser.EQ, 0); }
		public PredEqContext(PredTopContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitPredEq(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class PredParenContext extends PredTopContext {
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public PredParenContext(PredTopContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitPredParen(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PredTopContext predTop() throws RecognitionException {
		PredTopContext _localctx = new PredTopContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_predTop);
		try {
			setState(235);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				_localctx = new PredParenContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(219);
				match(T__2);
				setState(220);
				predicate();
				setState(221);
				match(T__3);
				}
				break;
			case 2:
				_localctx = new PredIneqContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(223);
				value();
				setState(224);
				match(INEQ);
				setState(225);
				value();
				}
				break;
			case 3:
				_localctx = new PredEqContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(227);
				value();
				setState(228);
				match(EQ);
				setState(229);
				value();
				}
				break;
			case 4:
				_localctx = new PredInContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(231);
				value();
				setState(232);
				match(IN);
				setState(233);
				subquery();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ValueContext extends ParserRuleContext {
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
	 
		public ValueContext() { }
		public void copyFrom(ValueContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ValueIdentifierContext extends ValueContext {
		public TerminalNode ID() { return getToken(RewriteAntlrDslParser.ID, 0); }
		public ValueIdentifierContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitValueIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ValueNumberContext extends ValueContext {
		public TerminalNode NUM() { return getToken(RewriteAntlrDslParser.NUM, 0); }
		public ValueNumberContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitValueNumber(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ValueFreshContext extends ValueContext {
		public TerminalNode FRESH() { return getToken(RewriteAntlrDslParser.FRESH, 0); }
		public TerminalNode NUM() { return getToken(RewriteAntlrDslParser.NUM, 0); }
		public ValueFreshContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitValueFresh(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ValueParameterContext extends ValueContext {
		public List<TerminalNode> INEQ() { return getTokens(RewriteAntlrDslParser.INEQ); }
		public TerminalNode INEQ(int i) {
			return getToken(RewriteAntlrDslParser.INEQ, i);
		}
		public TerminalNode ID() { return getToken(RewriteAntlrDslParser.ID, 0); }
		public ValueParameterContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitValueParameter(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ValueSubqueryContext extends ValueContext {
		public SubqueryContext subquery() {
			return getRuleContext(SubqueryContext.class,0);
		}
		public ValueSubqueryContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitValueSubquery(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_value);
		try {
			setState(249);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				_localctx = new ValueParameterContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(237);
				if (!( getCurrentToken().getText().equals("<") )) throw new FailedPredicateException(this, " getCurrentToken().getText().equals(\"<\") ");
				setState(238);
				match(INEQ);
				setState(239);
				match(ID);
				setState(240);
				if (!( getCurrentToken().getText().equals(">") )) throw new FailedPredicateException(this, " getCurrentToken().getText().equals(\">\") ");
				setState(241);
				match(INEQ);
				}
				break;
			case 2:
				_localctx = new ValueIdentifierContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(242);
				match(ID);
				}
				break;
			case 3:
				_localctx = new ValueNumberContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(243);
				match(NUM);
				}
				break;
			case 4:
				_localctx = new ValueSubqueryContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(244);
				subquery();
				}
				break;
			case 5:
				_localctx = new ValueFreshContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(245);
				match(FRESH);
				setState(246);
				match(T__2);
				setState(247);
				match(NUM);
				setState(248);
				match(T__3);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SubqueryContext extends ParserRuleContext {
		public TerminalNode SELECT() { return getToken(RewriteAntlrDslParser.SELECT, 0); }
		public ColumnContext column() {
			return getRuleContext(ColumnContext.class,0);
		}
		public TerminalNode FROM() { return getToken(RewriteAntlrDslParser.FROM, 0); }
		public TableContext table() {
			return getRuleContext(TableContext.class,0);
		}
		public List<JoinContext> join() {
			return getRuleContexts(JoinContext.class);
		}
		public JoinContext join(int i) {
			return getRuleContext(JoinContext.class,i);
		}
		public TerminalNode WHERE() { return getToken(RewriteAntlrDslParser.WHERE, 0); }
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public SubqueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subquery; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitSubquery(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SubqueryContext subquery() throws RecognitionException {
		SubqueryContext _localctx = new SubqueryContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_subquery);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(251);
			match(T__2);
			setState(252);
			match(SELECT);
			setState(253);
			column();
			setState(254);
			match(FROM);
			setState(255);
			table();
			setState(259);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==JOIN) {
				{
				{
				setState(256);
				join();
				}
				}
				setState(261);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(264);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(262);
				match(WHERE);
				setState(263);
				predicate();
				}
			}

			setState(266);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SchemaContext extends ParserRuleContext {
		public List<TableDefContext> tableDef() {
			return getRuleContexts(TableDefContext.class);
		}
		public TableDefContext tableDef(int i) {
			return getRuleContext(TableDefContext.class,i);
		}
		public SchemaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_schema; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitSchema(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SchemaContext schema() throws RecognitionException {
		SchemaContext _localctx = new SchemaContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_schema);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(271);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CREATE) {
				{
				{
				setState(268);
				tableDef();
				}
				}
				setState(273);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TableDefContext extends ParserRuleContext {
		public Token tableName;
		public TerminalNode CREATE() { return getToken(RewriteAntlrDslParser.CREATE, 0); }
		public TerminalNode TABLE() { return getToken(RewriteAntlrDslParser.TABLE, 0); }
		public TerminalNode ID() { return getToken(RewriteAntlrDslParser.ID, 0); }
		public List<ColumnStmtContext> columnStmt() {
			return getRuleContexts(ColumnStmtContext.class);
		}
		public ColumnStmtContext columnStmt(int i) {
			return getRuleContext(ColumnStmtContext.class,i);
		}
		public TableDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tableDef; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitTableDef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TableDefContext tableDef() throws RecognitionException {
		TableDefContext _localctx = new TableDefContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_tableDef);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(274);
			match(CREATE);
			setState(275);
			match(TABLE);
			setState(276);
			((TableDefContext)_localctx).tableName = match(ID);
			setState(277);
			match(T__2);
			setState(289);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PRIMARY) | (1L << FOREIGN) | (1L << ID))) != 0)) {
				{
				setState(278);
				columnStmt();
				setState(283);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(279);
						match(T__0);
						setState(280);
						columnStmt();
						}
						} 
					}
					setState(285);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
				}
				setState(287);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__0) {
					{
					setState(286);
					match(T__0);
					}
				}

				}
			}

			setState(291);
			match(T__3);
			setState(292);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ColumnStmtContext extends ParserRuleContext {
		public ColumnStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_columnStmt; }
	 
		public ColumnStmtContext() { }
		public void copyFrom(ColumnStmtContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class PkDefContext extends ColumnStmtContext {
		public Token columnName;
		public TerminalNode PRIMARY() { return getToken(RewriteAntlrDslParser.PRIMARY, 0); }
		public TerminalNode KEY() { return getToken(RewriteAntlrDslParser.KEY, 0); }
		public TerminalNode ID() { return getToken(RewriteAntlrDslParser.ID, 0); }
		public PkDefContext(ColumnStmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitPkDef(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ColumnDefContext extends ColumnStmtContext {
		public Token name;
		public Token type;
		public List<TerminalNode> ID() { return getTokens(RewriteAntlrDslParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(RewriteAntlrDslParser.ID, i);
		}
		public ColumnDefContext(ColumnStmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitColumnDef(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FkDefContext extends ColumnStmtContext {
		public Token columnName;
		public Token destTable;
		public Token destColumn;
		public TerminalNode FOREIGN() { return getToken(RewriteAntlrDslParser.FOREIGN, 0); }
		public TerminalNode KEY() { return getToken(RewriteAntlrDslParser.KEY, 0); }
		public TerminalNode REFERENCES() { return getToken(RewriteAntlrDslParser.REFERENCES, 0); }
		public List<TerminalNode> ID() { return getTokens(RewriteAntlrDslParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(RewriteAntlrDslParser.ID, i);
		}
		public FkDefContext(ColumnStmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitFkDef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ColumnStmtContext columnStmt() throws RecognitionException {
		ColumnStmtContext _localctx = new ColumnStmtContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_columnStmt);
		try {
			setState(311);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				_localctx = new ColumnDefContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(294);
				((ColumnDefContext)_localctx).name = match(ID);
				setState(295);
				((ColumnDefContext)_localctx).type = match(ID);
				}
				break;
			case PRIMARY:
				_localctx = new PkDefContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(296);
				match(PRIMARY);
				setState(297);
				match(KEY);
				setState(298);
				match(T__2);
				setState(299);
				((PkDefContext)_localctx).columnName = match(ID);
				setState(300);
				match(T__3);
				}
				break;
			case FOREIGN:
				_localctx = new FkDefContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(301);
				match(FOREIGN);
				setState(302);
				match(KEY);
				setState(303);
				match(T__2);
				setState(304);
				((FkDefContext)_localctx).columnName = match(ID);
				setState(305);
				match(T__3);
				setState(306);
				match(REFERENCES);
				setState(307);
				((FkDefContext)_localctx).destTable = match(ID);
				setState(308);
				match(T__2);
				setState(309);
				((FkDefContext)_localctx).destColumn = match(ID);
				setState(310);
				match(T__3);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TupleContext extends ParserRuleContext {
		public Token tableName;
		public Token optionalMark;
		public TerminalNode ID() { return getToken(RewriteAntlrDslParser.ID, 0); }
		public List<PairContext> pair() {
			return getRuleContexts(PairContext.class);
		}
		public PairContext pair(int i) {
			return getRuleContext(PairContext.class,i);
		}
		public TupleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tuple; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitTuple(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TupleContext tuple() throws RecognitionException {
		TupleContext _localctx = new TupleContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_tuple);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(313);
			((TupleContext)_localctx).tableName = match(ID);
			setState(314);
			match(T__2);
			setState(323);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(315);
				pair();
				setState(320);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(316);
					match(T__0);
					setState(317);
					pair();
					}
					}
					setState(322);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(325);
			match(T__3);
			setState(327);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(326);
				((TupleContext)_localctx).optionalMark = match(T__4);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PairContext extends ParserRuleContext {
		public Token name;
		public Token valueLabel;
		public List<TerminalNode> ID() { return getTokens(RewriteAntlrDslParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(RewriteAntlrDslParser.ID, i);
		}
		public TerminalNode EQ() { return getToken(RewriteAntlrDslParser.EQ, 0); }
		public PairContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pair; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitPair(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PairContext pair() throws RecognitionException {
		PairContext _localctx = new PairContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_pair);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(329);
			((PairContext)_localctx).name = match(ID);
			setState(332);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EQ) {
				{
				setState(330);
				match(EQ);
				setState(331);
				((PairContext)_localctx).valueLabel = match(ID);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TupleListContext extends ParserRuleContext {
		public List<TupleContext> tuple() {
			return getRuleContexts(TupleContext.class);
		}
		public TupleContext tuple(int i) {
			return getRuleContext(TupleContext.class,i);
		}
		public TupleListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tupleList; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitTupleList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TupleListContext tupleList() throws RecognitionException {
		TupleListContext _localctx = new TupleListContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_tupleList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(334);
			tuple();
			setState(339);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(335);
				match(T__0);
				setState(336);
				tuple();
				}
				}
				setState(341);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MappingContext extends ParserRuleContext {
		public TupleListContext sourceTuples;
		public TupleListContext destTuples;
		public List<TupleListContext> tupleList() {
			return getRuleContexts(TupleListContext.class);
		}
		public TupleListContext tupleList(int i) {
			return getRuleContext(TupleListContext.class,i);
		}
		public MappingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mapping; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitMapping(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MappingContext mapping() throws RecognitionException {
		MappingContext _localctx = new MappingContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_mapping);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(342);
			((MappingContext)_localctx).sourceTuples = tupleList();
			setState(343);
			match(T__5);
			setState(344);
			((MappingContext)_localctx).destTuples = tupleList();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TableMappingContext extends ParserRuleContext {
		public List<MappingContext> mapping() {
			return getRuleContexts(MappingContext.class);
		}
		public MappingContext mapping(int i) {
			return getRuleContext(MappingContext.class,i);
		}
		public TableMappingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tableMapping; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitTableMapping(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TableMappingContext tableMapping() throws RecognitionException {
		TableMappingContext _localctx = new TableMappingContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_tableMapping);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(349);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ID) {
				{
				{
				setState(346);
				mapping();
				}
				}
				setState(351);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ProgramContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(RewriteAntlrDslParser.EOF, 0); }
		public List<MethodContext> method() {
			return getRuleContexts(MethodContext.class);
		}
		public MethodContext method(int i) {
			return getRuleContext(MethodContext.class,i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitProgram(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_program);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(355);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==UPDATE || _la==QUERY) {
				{
				{
				setState(352);
				method();
				}
				}
				setState(357);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(358);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SignatureContext extends ParserRuleContext {
		public Token type;
		public Token name;
		public TerminalNode ID() { return getToken(RewriteAntlrDslParser.ID, 0); }
		public TerminalNode UPDATE() { return getToken(RewriteAntlrDslParser.UPDATE, 0); }
		public TerminalNode QUERY() { return getToken(RewriteAntlrDslParser.QUERY, 0); }
		public List<ParameterContext> parameter() {
			return getRuleContexts(ParameterContext.class);
		}
		public ParameterContext parameter(int i) {
			return getRuleContext(ParameterContext.class,i);
		}
		public SignatureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_signature; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitSignature(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SignatureContext signature() throws RecognitionException {
		SignatureContext _localctx = new SignatureContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_signature);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(360);
			((SignatureContext)_localctx).type = _input.LT(1);
			_la = _input.LA(1);
			if ( !(_la==UPDATE || _la==QUERY) ) {
				((SignatureContext)_localctx).type = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(361);
			((SignatureContext)_localctx).name = match(ID);
			setState(362);
			match(T__2);
			setState(371);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(363);
				parameter();
				setState(368);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(364);
					match(T__0);
					setState(365);
					parameter();
					}
					}
					setState(370);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(373);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MethodContext extends ParserRuleContext {
		public SignatureContext signature() {
			return getRuleContext(SignatureContext.class,0);
		}
		public QueryListContext queryList() {
			return getRuleContext(QueryListContext.class,0);
		}
		public MethodContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_method; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitMethod(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MethodContext method() throws RecognitionException {
		MethodContext _localctx = new MethodContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_method);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(375);
			signature();
			setState(376);
			match(T__6);
			setState(377);
			queryList();
			setState(378);
			match(T__7);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParameterContext extends ParserRuleContext {
		public Token type;
		public Token name;
		public List<TerminalNode> ID() { return getTokens(RewriteAntlrDslParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(RewriteAntlrDslParser.ID, i);
		}
		public ParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameter; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RewriteAntlrDslVisitor ) return ((RewriteAntlrDslVisitor<? extends T>)visitor).visitParameter(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParameterContext parameter() throws RecognitionException {
		ParameterContext _localctx = new ParameterContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_parameter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(380);
			((ParameterContext)_localctx).type = match(ID);
			setState(381);
			((ParameterContext)_localctx).name = match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 11:
			return value_sempred((ValueContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean value_sempred(ValueContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return  getCurrentToken().getText().equals("<") ;
		case 1:
			return  getCurrentToken().getText().equals(">") ;
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\'\u0182\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\3\2\7\2\66\n\2\f\2\16\29\13\2\3\3\3\3\3\3\3\3\7\3?\n\3\f\3"+
		"\16\3B\13\3\3\3\3\3\3\3\7\3G\n\3\f\3\16\3J\13\3\3\3\3\3\5\3N\n\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\7\3Y\n\3\f\3\16\3\\\13\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\7\3d\n\3\f\3\16\3g\13\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3"+
		"q\n\3\3\3\3\3\3\3\3\3\3\3\3\3\7\3y\n\3\f\3\16\3|\13\3\3\3\3\3\3\3\7\3"+
		"\u0081\n\3\f\3\16\3\u0084\13\3\3\3\3\3\5\3\u0088\n\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\7\3\u0092\n\3\f\3\16\3\u0095\13\3\3\3\3\3\5\3\u0099\n\3"+
		"\3\3\3\3\3\3\3\3\3\3\6\3\u00a0\n\3\r\3\16\3\u00a1\3\3\3\3\3\3\3\3\7\3"+
		"\u00a8\n\3\f\3\16\3\u00ab\13\3\3\3\3\3\5\3\u00af\n\3\3\3\3\3\5\3\u00b3"+
		"\n\3\3\4\3\4\3\5\3\5\3\5\5\5\u00ba\n\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7"+
		"\3\7\3\7\3\7\3\b\3\b\3\t\3\t\3\t\7\t\u00cc\n\t\f\t\16\t\u00cf\13\t\3\n"+
		"\3\n\3\n\7\n\u00d4\n\n\f\n\16\n\u00d7\13\n\3\13\3\13\3\13\5\13\u00dc\n"+
		"\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\5"+
		"\f\u00ee\n\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\5\r\u00fc"+
		"\n\r\3\16\3\16\3\16\3\16\3\16\3\16\7\16\u0104\n\16\f\16\16\16\u0107\13"+
		"\16\3\16\3\16\5\16\u010b\n\16\3\16\3\16\3\17\7\17\u0110\n\17\f\17\16\17"+
		"\u0113\13\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\7\20\u011c\n\20\f\20\16"+
		"\20\u011f\13\20\3\20\5\20\u0122\n\20\5\20\u0124\n\20\3\20\3\20\3\20\3"+
		"\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3"+
		"\21\3\21\3\21\5\21\u013a\n\21\3\22\3\22\3\22\3\22\3\22\7\22\u0141\n\22"+
		"\f\22\16\22\u0144\13\22\5\22\u0146\n\22\3\22\3\22\5\22\u014a\n\22\3\23"+
		"\3\23\3\23\5\23\u014f\n\23\3\24\3\24\3\24\7\24\u0154\n\24\f\24\16\24\u0157"+
		"\13\24\3\25\3\25\3\25\3\25\3\26\7\26\u015e\n\26\f\26\16\26\u0161\13\26"+
		"\3\27\7\27\u0164\n\27\f\27\16\27\u0167\13\27\3\27\3\27\3\30\3\30\3\30"+
		"\3\30\3\30\3\30\7\30\u0171\n\30\f\30\16\30\u0174\13\30\5\30\u0176\n\30"+
		"\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\32\3\32\3\32\3\32\2\2\33\2\4\6\b"+
		"\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\2\3\4\2\20\20!!\u0198\2"+
		"\67\3\2\2\2\4\u00b2\3\2\2\2\6\u00b4\3\2\2\2\b\u00b6\3\2\2\2\n\u00bb\3"+
		"\2\2\2\f\u00c2\3\2\2\2\16\u00c6\3\2\2\2\20\u00c8\3\2\2\2\22\u00d0\3\2"+
		"\2\2\24\u00db\3\2\2\2\26\u00ed\3\2\2\2\30\u00fb\3\2\2\2\32\u00fd\3\2\2"+
		"\2\34\u0111\3\2\2\2\36\u0114\3\2\2\2 \u0139\3\2\2\2\"\u013b\3\2\2\2$\u014b"+
		"\3\2\2\2&\u0150\3\2\2\2(\u0158\3\2\2\2*\u015f\3\2\2\2,\u0165\3\2\2\2."+
		"\u016a\3\2\2\2\60\u0179\3\2\2\2\62\u017e\3\2\2\2\64\66\5\4\3\2\65\64\3"+
		"\2\2\2\669\3\2\2\2\67\65\3\2\2\2\678\3\2\2\28\3\3\2\2\29\67\3\2\2\2:;"+
		"\7\17\2\2;@\5\6\4\2<=\7\3\2\2=?\5\6\4\2><\3\2\2\2?B\3\2\2\2@>\3\2\2\2"+
		"@A\3\2\2\2AC\3\2\2\2B@\3\2\2\2CD\7\26\2\2DH\5\b\5\2EG\5\n\6\2FE\3\2\2"+
		"\2GJ\3\2\2\2HF\3\2\2\2HI\3\2\2\2IM\3\2\2\2JH\3\2\2\2KL\7\31\2\2LN\5\16"+
		"\b\2MK\3\2\2\2MN\3\2\2\2NO\3\2\2\2OP\7\4\2\2P\u00b3\3\2\2\2QR\7\21\2\2"+
		"RS\7\27\2\2ST\5\b\5\2TU\7\5\2\2UZ\5\6\4\2VW\7\3\2\2WY\5\6\4\2XV\3\2\2"+
		"\2Y\\\3\2\2\2ZX\3\2\2\2Z[\3\2\2\2[]\3\2\2\2\\Z\3\2\2\2]^\7\6\2\2^_\7\32"+
		"\2\2_`\7\5\2\2`e\5\30\r\2ab\7\3\2\2bd\5\30\r\2ca\3\2\2\2dg\3\2\2\2ec\3"+
		"\2\2\2ef\3\2\2\2fh\3\2\2\2ge\3\2\2\2hi\7\6\2\2ij\7\4\2\2j\u00b3\3\2\2"+
		"\2kl\7\22\2\2lm\7\26\2\2mp\5\b\5\2no\7\31\2\2oq\5\16\b\2pn\3\2\2\2pq\3"+
		"\2\2\2qr\3\2\2\2rs\7\4\2\2s\u00b3\3\2\2\2tu\7\22\2\2uz\5\b\5\2vw\7\3\2"+
		"\2wy\5\b\5\2xv\3\2\2\2y|\3\2\2\2zx\3\2\2\2z{\3\2\2\2{}\3\2\2\2|z\3\2\2"+
		"\2}~\7\26\2\2~\u0082\5\b\5\2\177\u0081\5\n\6\2\u0080\177\3\2\2\2\u0081"+
		"\u0084\3\2\2\2\u0082\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0087\3\2"+
		"\2\2\u0084\u0082\3\2\2\2\u0085\u0086\7\31\2\2\u0086\u0088\5\16\b\2\u0087"+
		"\u0085\3\2\2\2\u0087\u0088\3\2\2\2\u0088\u0089\3\2\2\2\u0089\u008a\7\4"+
		"\2\2\u008a\u00b3\3\2\2\2\u008b\u008c\7\20\2\2\u008c\u008d\5\b\5\2\u008d"+
		"\u008e\7\25\2\2\u008e\u0093\5\f\7\2\u008f\u0090\7\3\2\2\u0090\u0092\5"+
		"\f\7\2\u0091\u008f\3\2\2\2\u0092\u0095\3\2\2\2\u0093\u0091\3\2\2\2\u0093"+
		"\u0094\3\2\2\2\u0094\u0098\3\2\2\2\u0095\u0093\3\2\2\2\u0096\u0097\7\31"+
		"\2\2\u0097\u0099\5\16\b\2\u0098\u0096\3\2\2\2\u0098\u0099\3\2\2\2\u0099"+
		"\u009a\3\2\2\2\u009a\u009b\7\4\2\2\u009b\u00b3\3\2\2\2\u009c\u009d\7\20"+
		"\2\2\u009d\u009f\5\b\5\2\u009e\u00a0\5\n\6\2\u009f\u009e\3\2\2\2\u00a0"+
		"\u00a1\3\2\2\2\u00a1\u009f\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a2\u00a3\3\2"+
		"\2\2\u00a3\u00a4\7\25\2\2\u00a4\u00a9\5\f\7\2\u00a5\u00a6\7\3\2\2\u00a6"+
		"\u00a8\5\f\7\2\u00a7\u00a5\3\2\2\2\u00a8\u00ab\3\2\2\2\u00a9\u00a7\3\2"+
		"\2\2\u00a9\u00aa\3\2\2\2\u00aa\u00ae\3\2\2\2\u00ab\u00a9\3\2\2\2\u00ac"+
		"\u00ad\7\31\2\2\u00ad\u00af\5\16\b\2\u00ae\u00ac\3\2\2\2\u00ae\u00af\3"+
		"\2\2\2\u00af\u00b0\3\2\2\2\u00b0\u00b1\7\4\2\2\u00b1\u00b3\3\2\2\2\u00b2"+
		":\3\2\2\2\u00b2Q\3\2\2\2\u00b2k\3\2\2\2\u00b2t\3\2\2\2\u00b2\u008b\3\2"+
		"\2\2\u00b2\u009c\3\2\2\2\u00b3\5\3\2\2\2\u00b4\u00b5\7%\2\2\u00b5\7\3"+
		"\2\2\2\u00b6\u00b9\7%\2\2\u00b7\u00b8\7\23\2\2\u00b8\u00ba\7%\2\2\u00b9"+
		"\u00b7\3\2\2\2\u00b9\u00ba\3\2\2\2\u00ba\t\3\2\2\2\u00bb\u00bc\7\30\2"+
		"\2\u00bc\u00bd\5\b\5\2\u00bd\u00be\7\24\2\2\u00be\u00bf\5\6\4\2\u00bf"+
		"\u00c0\7#\2\2\u00c0\u00c1\5\6\4\2\u00c1\13\3\2\2\2\u00c2\u00c3\5\6\4\2"+
		"\u00c3\u00c4\7#\2\2\u00c4\u00c5\5\30\r\2\u00c5\r\3\2\2\2\u00c6\u00c7\5"+
		"\20\t\2\u00c7\17\3\2\2\2\u00c8\u00cd\5\22\n\2\u00c9\u00ca\7\r\2\2\u00ca"+
		"\u00cc\5\22\n\2\u00cb\u00c9\3\2\2\2\u00cc\u00cf\3\2\2\2\u00cd\u00cb\3"+
		"\2\2\2\u00cd\u00ce\3\2\2\2\u00ce\21\3\2\2\2\u00cf\u00cd\3\2\2\2\u00d0"+
		"\u00d5\5\24\13\2\u00d1\u00d2\7\f\2\2\u00d2\u00d4\5\24\13\2\u00d3\u00d1"+
		"\3\2\2\2\u00d4\u00d7\3\2\2\2\u00d5\u00d3\3\2\2\2\u00d5\u00d6\3\2\2\2\u00d6"+
		"\23\3\2\2\2\u00d7\u00d5\3\2\2\2\u00d8\u00dc\5\26\f\2\u00d9\u00da\7\16"+
		"\2\2\u00da\u00dc\5\24\13\2\u00db\u00d8\3\2\2\2\u00db\u00d9\3\2\2\2\u00dc"+
		"\25\3\2\2\2\u00dd\u00de\7\5\2\2\u00de\u00df\5\16\b\2\u00df\u00e0\7\6\2"+
		"\2\u00e0\u00ee\3\2\2\2\u00e1\u00e2\5\30\r\2\u00e2\u00e3\7$\2\2\u00e3\u00e4"+
		"\5\30\r\2\u00e4\u00ee\3\2\2\2\u00e5\u00e6\5\30\r\2\u00e6\u00e7\7#\2\2"+
		"\u00e7\u00e8\5\30\r\2\u00e8\u00ee\3\2\2\2\u00e9\u00ea\5\30\r\2\u00ea\u00eb"+
		"\7\13\2\2\u00eb\u00ec\5\32\16\2\u00ec\u00ee\3\2\2\2\u00ed\u00dd\3\2\2"+
		"\2\u00ed\u00e1\3\2\2\2\u00ed\u00e5\3\2\2\2\u00ed\u00e9\3\2\2\2\u00ee\27"+
		"\3\2\2\2\u00ef\u00f0\6\r\2\2\u00f0\u00f1\7$\2\2\u00f1\u00f2\7%\2\2\u00f2"+
		"\u00f3\6\r\3\2\u00f3\u00fc\7$\2\2\u00f4\u00fc\7%\2\2\u00f5\u00fc\7&\2"+
		"\2\u00f6\u00fc\5\32\16\2\u00f7\u00f8\7\"\2\2\u00f8\u00f9\7\5\2\2\u00f9"+
		"\u00fa\7&\2\2\u00fa\u00fc\7\6\2\2\u00fb\u00ef\3\2\2\2\u00fb\u00f4\3\2"+
		"\2\2\u00fb\u00f5\3\2\2\2\u00fb\u00f6\3\2\2\2\u00fb\u00f7\3\2\2\2\u00fc"+
		"\31\3\2\2\2\u00fd\u00fe\7\5\2\2\u00fe\u00ff\7\17\2\2\u00ff\u0100\5\6\4"+
		"\2\u0100\u0101\7\26\2\2\u0101\u0105\5\b\5\2\u0102\u0104\5\n\6\2\u0103"+
		"\u0102\3\2\2\2\u0104\u0107\3\2\2\2\u0105\u0103\3\2\2\2\u0105\u0106\3\2"+
		"\2\2\u0106\u010a\3\2\2\2\u0107\u0105\3\2\2\2\u0108\u0109\7\31\2\2\u0109"+
		"\u010b\5\16\b\2\u010a\u0108\3\2\2\2\u010a\u010b\3\2\2\2\u010b\u010c\3"+
		"\2\2\2\u010c\u010d\7\6\2\2\u010d\33\3\2\2\2\u010e\u0110\5\36\20\2\u010f"+
		"\u010e\3\2\2\2\u0110\u0113\3\2\2\2\u0111\u010f\3\2\2\2\u0111\u0112\3\2"+
		"\2\2\u0112\35\3\2\2\2\u0113\u0111\3\2\2\2\u0114\u0115\7\33\2\2\u0115\u0116"+
		"\7\34\2\2\u0116\u0117\7%\2\2\u0117\u0123\7\5\2\2\u0118\u011d\5 \21\2\u0119"+
		"\u011a\7\3\2\2\u011a\u011c\5 \21\2\u011b\u0119\3\2\2\2\u011c\u011f\3\2"+
		"\2\2\u011d\u011b\3\2\2\2\u011d\u011e\3\2\2\2\u011e\u0121\3\2\2\2\u011f"+
		"\u011d\3\2\2\2\u0120\u0122\7\3\2\2\u0121\u0120\3\2\2\2\u0121\u0122\3\2"+
		"\2\2\u0122\u0124\3\2\2\2\u0123\u0118\3\2\2\2\u0123\u0124\3\2\2\2\u0124"+
		"\u0125\3\2\2\2\u0125\u0126\7\6\2\2\u0126\u0127\7\4\2\2\u0127\37\3\2\2"+
		"\2\u0128\u0129\7%\2\2\u0129\u013a\7%\2\2\u012a\u012b\7\35\2\2\u012b\u012c"+
		"\7\37\2\2\u012c\u012d\7\5\2\2\u012d\u012e\7%\2\2\u012e\u013a\7\6\2\2\u012f"+
		"\u0130\7\36\2\2\u0130\u0131\7\37\2\2\u0131\u0132\7\5\2\2\u0132\u0133\7"+
		"%\2\2\u0133\u0134\7\6\2\2\u0134\u0135\7 \2\2\u0135\u0136\7%\2\2\u0136"+
		"\u0137\7\5\2\2\u0137\u0138\7%\2\2\u0138\u013a\7\6\2\2\u0139\u0128\3\2"+
		"\2\2\u0139\u012a\3\2\2\2\u0139\u012f\3\2\2\2\u013a!\3\2\2\2\u013b\u013c"+
		"\7%\2\2\u013c\u0145\7\5\2\2\u013d\u0142\5$\23\2\u013e\u013f\7\3\2\2\u013f"+
		"\u0141\5$\23\2\u0140\u013e\3\2\2\2\u0141\u0144\3\2\2\2\u0142\u0140\3\2"+
		"\2\2\u0142\u0143\3\2\2\2\u0143\u0146\3\2\2\2\u0144\u0142\3\2\2\2\u0145"+
		"\u013d\3\2\2\2\u0145\u0146\3\2\2\2\u0146\u0147\3\2\2\2\u0147\u0149\7\6"+
		"\2\2\u0148\u014a\7\7\2\2\u0149\u0148\3\2\2\2\u0149\u014a\3\2\2\2\u014a"+
		"#\3\2\2\2\u014b\u014e\7%\2\2\u014c\u014d\7#\2\2\u014d\u014f\7%\2\2\u014e"+
		"\u014c\3\2\2\2\u014e\u014f\3\2\2\2\u014f%\3\2\2\2\u0150\u0155\5\"\22\2"+
		"\u0151\u0152\7\3\2\2\u0152\u0154\5\"\22\2\u0153\u0151\3\2\2\2\u0154\u0157"+
		"\3\2\2\2\u0155\u0153\3\2\2\2\u0155\u0156\3\2\2\2\u0156\'\3\2\2\2\u0157"+
		"\u0155\3\2\2\2\u0158\u0159\5&\24\2\u0159\u015a\7\b\2\2\u015a\u015b\5&"+
		"\24\2\u015b)\3\2\2\2\u015c\u015e\5(\25\2\u015d\u015c\3\2\2\2\u015e\u0161"+
		"\3\2\2\2\u015f\u015d\3\2\2\2\u015f\u0160\3\2\2\2\u0160+\3\2\2\2\u0161"+
		"\u015f\3\2\2\2\u0162\u0164\5\60\31\2\u0163\u0162\3\2\2\2\u0164\u0167\3"+
		"\2\2\2\u0165\u0163\3\2\2\2\u0165\u0166\3\2\2\2\u0166\u0168\3\2\2\2\u0167"+
		"\u0165\3\2\2\2\u0168\u0169\7\2\2\3\u0169-\3\2\2\2\u016a\u016b\t\2\2\2"+
		"\u016b\u016c\7%\2\2\u016c\u0175\7\5\2\2\u016d\u0172\5\62\32\2\u016e\u016f"+
		"\7\3\2\2\u016f\u0171\5\62\32\2\u0170\u016e\3\2\2\2\u0171\u0174\3\2\2\2"+
		"\u0172\u0170\3\2\2\2\u0172\u0173\3\2\2\2\u0173\u0176\3\2\2\2\u0174\u0172"+
		"\3\2\2\2\u0175\u016d\3\2\2\2\u0175\u0176\3\2\2\2\u0176\u0177\3\2\2\2\u0177"+
		"\u0178\7\6\2\2\u0178/\3\2\2\2\u0179\u017a\5.\30\2\u017a\u017b\7\t\2\2"+
		"\u017b\u017c\5\2\2\2\u017c\u017d\7\n\2\2\u017d\61\3\2\2\2\u017e\u017f"+
		"\7%\2\2\u017f\u0180\7%\2\2\u0180\63\3\2\2\2(\67@HMZepz\u0082\u0087\u0093"+
		"\u0098\u00a1\u00a9\u00ae\u00b2\u00b9\u00cd\u00d5\u00db\u00ed\u00fb\u0105"+
		"\u010a\u0111\u011d\u0121\u0123\u0139\u0142\u0145\u0149\u014e\u0155\u015f"+
		"\u0165\u0172\u0175";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}