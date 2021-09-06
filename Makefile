# A2_Concurrency makefile
# Tafadzwa Nyazenga
# 26 February 2021


JAVA=/usr/bin/java
JAVAC=/usr/bin/javac
JAVADOC=/usr/bin/javadoc
.SUFFIXES: .java .class
SRCDIR=src
BINDIR=bin
DOCDIR=doc

#.PHONY: default doc clean

$(BINDIR)/%.class:$(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) $<

CLASSES= Score.class WordDictionary.class WordRecord.class WordPanel.class WordApp.class
CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)

default: $(CLASS_FILES)

### Documentation
doc:
		$(JAVADOC) -d $(DOCDIR) $(SRCDIR)/*

clean:
	$(RM) $(BINDIR)/*.class


run: $(CLASS_FILES)
		$(JAVA) -cp $(BINDIR) WordApp $(total_words) $(words_on_screen) $(dict_file)


clean-doc:
		rm -r $(DOCDIR)/*
