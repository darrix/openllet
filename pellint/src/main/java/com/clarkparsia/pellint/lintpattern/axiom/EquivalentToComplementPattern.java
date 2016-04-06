// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.lintpattern.axiom;

import com.clarkparsia.owlapi.OWL;

import java.util.Set;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import com.clarkparsia.pellint.format.CompactClassLintFormat;
import com.clarkparsia.pellint.format.LintFormat;
import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.model.LintFixer;

/**
 * <p>
 * Title: 
 * </p>
 * <p>
 * Description: 
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Harris Lin
 */
public class EquivalentToComplementPattern extends AxiomLintPattern {
	private static final LintFormat DEFAULT_LINT_FORMAT = new CompactClassLintFormat();
	
	public String getName() {
		return getClass().getSimpleName();
	}
	
	public String getDescription() {
		return "A named concept is equivalent to some complement";
	}

	public boolean isFixable() {
		return true;
	}
	
	public LintFormat getDefaultLintFormat() {
		return DEFAULT_LINT_FORMAT;
	}

	public void visit(OWLEquivalentClassesAxiom axiom) {
		Set<OWLClassExpression> owlDescs = axiom.getClassExpressions();
		if (owlDescs.size() != 2) return;
		
		OWLClass namedClass = null;
		OWLClassExpression complementOf = null;
		for (OWLClassExpression owlDesc : owlDescs) {
			if (!owlDesc.isAnonymous()) {
				namedClass = owlDesc.asOWLClass();
			} else if (owlDesc instanceof OWLObjectComplementOf) {
				complementOf = owlDesc;
			}
		}
		
		if (namedClass != null && complementOf != null) {
			Lint lint = makeLint();
			lint.addParticipatingClass(namedClass);
			lint.addParticipatingAxiom(axiom);
			OWLAxiom newAxiom = OWL.subClassOf(namedClass, complementOf);
			LintFixer fixer = new LintFixer(axiom, newAxiom);
			lint.setLintFixer(fixer);
			setLint(lint);
		}
	}
}