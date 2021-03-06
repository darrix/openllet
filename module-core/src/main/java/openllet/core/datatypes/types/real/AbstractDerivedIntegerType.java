package openllet.core.datatypes.types.real;

import openllet.aterm.ATermAppl;
import openllet.core.datatypes.AbstractBaseDatatype;
import openllet.core.datatypes.Datatype;
import openllet.core.datatypes.OWLRealUtils;
import openllet.core.datatypes.RestrictedDatatype;
import openllet.core.datatypes.exceptions.InvalidLiteralException;
import openllet.core.utils.ATermUtils;

/**
 * <p>
 * Title: Abstract derived integer type
 * </p>
 * <p>
 * Description: Base implementation for integer datatypes derived from <code>xsd:decimal</code>
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Mike Smith
 */
public abstract class AbstractDerivedIntegerType extends AbstractBaseDatatype<Number>
{
	private static final XSDDecimal XSD_DECIMAL = XSDDecimal.getInstance();
	private final RestrictedDatatype<Number> _dataRange;

	public AbstractDerivedIntegerType(final ATermAppl name, final Number lower, final Number upper)
	{
		super(name);

		if (lower != null && !OWLRealUtils.isInteger(lower))
			throw new IllegalArgumentException();
		if (upper != null && !OWLRealUtils.isInteger(upper))
			throw new IllegalArgumentException();
		if (lower != null && upper != null && OWLRealUtils.compare(lower, upper) > 0)
			throw new IllegalArgumentException();

		final IntegerInterval i = new IntegerInterval(lower == null ? null : OWLRealUtils.getCanonicalObject(lower), upper == null ? null : OWLRealUtils.getCanonicalObject(upper));
		_dataRange = new RestrictedRealDatatype(this, i, null, null);
	}

	@Override
	public RestrictedDatatype<Number> asDataRange()
	{
		return _dataRange;
	}

	/**
	 * Parse and validate a lexical form of the literal.
	 *
	 * @param lexicalForm
	 * @return a <code>Number</code> representation of the literal
	 * @throws InvalidLiteralException if the literal form is invalid or the value is out of range
	 */
	protected abstract Number fromLexicalForm(String lexicalForm) throws InvalidLiteralException;

	@Override
	public ATermAppl getCanonicalRepresentation(final ATermAppl input) throws InvalidLiteralException
	{
		final String lexicalForm = getLexicalForm(input);
		fromLexicalForm(lexicalForm);
		return XSD_DECIMAL.getCanonicalRepresentation(ATermUtils.makeTypedLiteral(lexicalForm, XSD_DECIMAL.getName()));
	}

	@Override
	public ATermAppl getLiteral(final Object value)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Datatype<?> getPrimitiveDatatype()
	{
		return XSDDecimal.getInstance();
	}

	@Override
	public Number getValue(final ATermAppl literal) throws InvalidLiteralException
	{
		final String lexicalForm = getLexicalForm(literal);
		return OWLRealUtils.getCanonicalObject(fromLexicalForm(lexicalForm));
	}

	@Override
	public boolean isPrimitive()
	{
		return false;
	}
}
