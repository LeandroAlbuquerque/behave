/*
 * Demoiselle Framework
 * Copyright (C) 2013 SERPRO
 * ----------------------------------------------------------------------------
 * This file is part of Demoiselle Framework.
 * 
 * Demoiselle Framework is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License version 3
 * along with this program; if not,  see <http://www.gnu.org/licenses/>
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA  02110-1301, USA.
 * ----------------------------------------------------------------------------
 * Este arquivo é parte do Framework Demoiselle.
 * 
 * O Framework Demoiselle é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da GNU LGPL versão 3 como publicada pela Fundação
 * do Software Livre (FSF).
 * 
 * Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA
 * GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou
 * APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU/LGPL em português
 * para maiores detalhes.
 * 
 * Você deve ter recebido uma cópia da GNU LGPL versão 3, sob o título
 * "LICENCA.txt", junto com esse programa. Se não, acesse <http://www.gnu.org/licenses/>
 * ou escreva para a Fundação do Software Livre (FSF) Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02111-1301, USA.
 */
package br.gov.frameworkdemoiselle.behave.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.gov.frameworkdemoiselle.behave.config.BehaveConfig;
import br.gov.frameworkdemoiselle.behave.exception.BehaveException;
import br.gov.frameworkdemoiselle.behave.internal.parse.StoryFileConverter;
import br.gov.frameworkdemoiselle.behave.internal.spi.InjectionManager;
import br.gov.frameworkdemoiselle.behave.parser.Parser;
import br.gov.frameworkdemoiselle.behave.parser.Step;

public class BehaveController {
	
	private static BehaveController eng = new BehaveController();
			
	private Parser parser;

	Logger log = Logger.getLogger(BehaveController.class);

	private ArrayList<String> allOriginalStoriesPath = new ArrayList<String>();
	
	private List<Step> steps = new ArrayList<Step>();
	
	private List<String> storiesPath = new ArrayList<String>();
	
	private BehaveController(){
		
	}
	
	public static BehaveController getInstance(){
		return eng;
	}

	public void addSteps(Step step) {
		steps.add(step);
	}

	@SuppressWarnings("unchecked")
	public void run(List<String> storiesPath) {
		try {
			log.info("--------------------------------");
			log.info(" Iniciando Demoiselle Behave");
			log.info("--------------------------------");
			
			if (storiesPath == null || storiesPath.isEmpty()){
				throw new BehaveException("Lista de histórias vazias. Informe ao menos uma história");
			}
			// Armazena o array antigo para retirar as histórias depois
			List<String> oldsStories = (List<String>) allOriginalStoriesPath.clone();
			
			// Adiciono as novas histórias
			allOriginalStoriesPath.addAll(storiesPath);
			
			// Faz a conversão
			List<String> allStoriesConverted = StoryFileConverter.convertReusedScenarios(allOriginalStoriesPath, BehaveConfig.ORIGINAL_STORY_FILE_EXTENSION, BehaveConfig.CONVERTED_STORY_FILE_EXTENSION, true);

			// Cria um novo array contendo somente as histórias atuais, sem as antigas
			List<String> finalArray = new ArrayList<String>();			
			for (String s : allStoriesConverted) {
				if (!oldsStories.contains(s)) {
					finalArray.add(s);
				}
			}	
			// Roda o runner com as histórias convertidas			
			parser = (Parser) InjectionManager.getInstance().getInstanceDependecy(Parser.class);
			parser.setSteps(steps);
			parser.setStoryPaths(finalArray);
			parser.run();
		} catch (Exception ex) {
			log.error("Erro ao executar o Demoiselle Behave", ex);
		}finally{
			log.info("--------------------------------");
			log.info(" Desligando Demoiselle Behave");
			log.info("--------------------------------");
		}

	}

	public void run(String storiesPath) {
		ArrayList<String> stories = new ArrayList<String>();
		stories.add(storiesPath);
		run(stories);
	}

	public void run() {
		run(storiesPath);
	}

	public BehaveController addStories(String storiesPath) {
		log.debug("addStories:" + storiesPath);
		this.storiesPath.add(storiesPath);
		return this;
	}
}