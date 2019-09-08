// Generated from AntlrDsl.g4 by ANTLR 4.6

package migrator.bench.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class AntlrDslLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.6", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, MEM=6, LNOT=7, LAND=8, LOR=9,
		JOIN=10, SEL=11, PROJ=12, INS=13, DEL=14, UPD=15, LOP=16, ID=17, WS=18;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "MEM", "LNOT", "LAND", "LOR",
		"JOIN", "SEL", "PROJ", "INS", "DEL", "UPD", "LOP", "ID", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'('", "','", "')'", "'['", "']'", "'in'", "'not'", "'and'", "'or'",
		"'join'", "'sigma'", "'pi'", "'ins'", "'del'", "'upd'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, "MEM", "LNOT", "LAND", "LOR", "JOIN",
		"SEL", "PROJ", "INS", "DEL", "UPD", "LOP", "ID", "WS"
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


	public AntlrDslLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "AntlrDsl.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\24r\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3\b\3\b"+
		"\3\b\3\b\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3"+
		"\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\5\21c"+
		"\n\21\3\22\3\22\7\22g\n\22\f\22\16\22j\13\22\3\23\6\23m\n\23\r\23\16\23"+
		"n\3\23\3\23\2\2\24\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r"+
		"\31\16\33\17\35\20\37\21!\22#\23%\24\3\2\5\5\2C\\aac|\7\2/\60\62;C\\a"+
		"ac|\5\2\13\f\17\17\"\"x\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2"+
		"\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25"+
		"\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2"+
		"\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\3\'\3\2\2\2\5)\3\2\2\2\7+\3\2\2"+
		"\2\t-\3\2\2\2\13/\3\2\2\2\r\61\3\2\2\2\17\64\3\2\2\2\218\3\2\2\2\23<\3"+
		"\2\2\2\25?\3\2\2\2\27D\3\2\2\2\31J\3\2\2\2\33M\3\2\2\2\35Q\3\2\2\2\37"+
		"U\3\2\2\2!b\3\2\2\2#d\3\2\2\2%l\3\2\2\2\'(\7*\2\2(\4\3\2\2\2)*\7.\2\2"+
		"*\6\3\2\2\2+,\7+\2\2,\b\3\2\2\2-.\7]\2\2.\n\3\2\2\2/\60\7_\2\2\60\f\3"+
		"\2\2\2\61\62\7k\2\2\62\63\7p\2\2\63\16\3\2\2\2\64\65\7p\2\2\65\66\7q\2"+
		"\2\66\67\7v\2\2\67\20\3\2\2\289\7c\2\29:\7p\2\2:;\7f\2\2;\22\3\2\2\2<"+
		"=\7q\2\2=>\7t\2\2>\24\3\2\2\2?@\7l\2\2@A\7q\2\2AB\7k\2\2BC\7p\2\2C\26"+
		"\3\2\2\2DE\7u\2\2EF\7k\2\2FG\7i\2\2GH\7o\2\2HI\7c\2\2I\30\3\2\2\2JK\7"+
		"r\2\2KL\7k\2\2L\32\3\2\2\2MN\7k\2\2NO\7p\2\2OP\7u\2\2P\34\3\2\2\2QR\7"+
		"f\2\2RS\7g\2\2ST\7n\2\2T\36\3\2\2\2UV\7w\2\2VW\7r\2\2WX\7f\2\2X \3\2\2"+
		"\2Yc\7?\2\2Z[\7#\2\2[c\7?\2\2\\c\7>\2\2]^\7>\2\2^c\7?\2\2_c\7@\2\2`a\7"+
		"@\2\2ac\7?\2\2bY\3\2\2\2bZ\3\2\2\2b\\\3\2\2\2b]\3\2\2\2b_\3\2\2\2b`\3"+
		"\2\2\2c\"\3\2\2\2dh\t\2\2\2eg\t\3\2\2fe\3\2\2\2gj\3\2\2\2hf\3\2\2\2hi"+
		"\3\2\2\2i$\3\2\2\2jh\3\2\2\2km\t\4\2\2lk\3\2\2\2mn\3\2\2\2nl\3\2\2\2n"+
		"o\3\2\2\2op\3\2\2\2pq\b\23\2\2q&\3\2\2\2\6\2bhn\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}