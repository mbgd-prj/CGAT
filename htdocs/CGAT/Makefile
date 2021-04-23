DIST_DIR = ../htdocs/CGAT
INST_DIR = $(DIST_DIR)/manual

all:	html html1 ps txt

install_man:	all
	mkdir -p $(INST_DIR)
	rsync -r . $(INST_DIR)

install_man_dist:	all
	cd $(DIST_DIR); ln -s ../../docs manual

html1:	manual.html

html:	book1.html

ps:	manual.ps

txt:	manual.txt

man:	manual.man

manual.html:	manual.xml
	jw -u -f docbook -b html manual.xml

book1.html:	manual.xml
	jw -f docbook -b html manual.xml

manual.ps:	manual.xml
	docbook2ps manual.xml

manual.txt:	manual.xml
	docbook2txt manual.xml

manual.man:	manual.xml
	docbook2man manual.xml

clean:
	rm *.html *.ps *.pdf *.txt
