CREATE EXTENSION "uuid-ossp";

DROP TABLE loops;

CREATE TABLE loops (
	uid UUID NOT NULL DEFAULT uuid_generate_v4(),
	lid TEXT, 
	content XML,
	created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
	updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
	created_by TEXT, 
	updated_by TEXT, 
	CONSTRAINT pk_loop PRIMARY KEY (uid)
);

DELETE from loops;
INSERT INTO loops(lid, content, created_by) values('#journal', '<loop><body>This is my journal <tag type="usertag">@pd</tag><tags-box>Tags go here</tags-box></body></loop>', 'pdrummond');
INSERT INTO loops(lid, content, created_by) values('#entry1', '<loop><body>This is my first <tag type="hashtag">#journal</tag> entry. <tag type="usertag">@pd</tag></body></loop>', 'pdrummond');
INSERT INTO loops(lid, content, created_by) values('#random', '<loop><body><tag type="usertag">@pd</tag><tag type="usertag">@po</tag>Random loop</body></loop>', 'pdrummond');
INSERT INTO loops(lid, content, created_by) values('#entry2', '<loop><body>This is my second <tag type="hashtag">#journal</tag> entry. <tag type="usertag">@pd</tag></body></loop>', 'pdrummond');

SELECT id, (xpath('//tag[@type="usertag"]/text()', content)) from loops;

UPDATE loops SET id = '#journal2', content = '<?xml version="1.0" encoding="UTF-8"?>
<loop>
  <body>
    <h1>#important2</h1>
  </body>
  <tags-box />
</loop>' WHERE id = '#journal';



SELECT id, (xpath('//tag[@type="usertag"]/text()', content)) from loops;
SELECT id from loops where (xpath('//tag[@type="usertag"]/text()', content));   

SELECT id from loops where xpath('//tag[text() = "pd"] | //tag[text() = "po"]', content) @> ('pd', 'po');

SELECT id, content ::text from loops where (xpath('//tag[text() = "pd"] | //tag[text() = "po"]', content)); == 2;

SELECT id, content ::text from loops where (xpath('//tag[@type="usertag"]/text()', content))[1]::text in ('pd', 'po');  

SELECT id, created_at, content ::text from loops;

SELECT id, content from loops l, refs r where r.ref_tag_id = l.id and l.id = 'journal';

SELECT id, content ::text, created_at, created_by from loops l, refs r WHERE r.ref_id = l.id AND r.ref_tag_id = 'journal';

select * from refs;

select id from loops;

SELECT uid, lid, content ::text from loops;

SELECT * FROM loops WHERE content LIKE '%boom%'

UPDATE loops SET content = 'This is loop11' WHERE lid = 'loop11'

SELECT * FROM loops WHERE content LIKE '%#journal%'