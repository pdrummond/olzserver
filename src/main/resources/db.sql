DROP TABLE refs;
DROP TABLE tags;
DROP TABLE loops;

CREATE TABLE loops (	
	loop_id TEXT, 
	loop_content XML,
	loop_created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
	loop_created_by TEXT, 
	CONSTRAINT pk_loop PRIMARY KEY (loop_id)
);

CREATE TABLE tags (
	tag_id TEXT, 
	tag_type INTEGER,
	tag_created_by TEXT,
	tag_created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
); 

CREATE TABLE refs (
	ref_id BIGSERIAL, 
	ref_loop_id TEXT,
	ref_tag_id TEXT, 
	ref_created_by TEXT, 
	ref_created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
	CONSTRAINT pk_ref PRIMARY KEY (ref_id),
	CONSTRAINT fk1_loops FOREIGN KEY (ref_loop_id) REFERENCES loops,
	CONSTRAINT fk1_tags FOREIGN KEY (ref_tag_id) REFERENCES tags
);

DELETE from loops where loop_id = '#journal';

DELETE from loops;
INSERT INTO loops(loop_id, loop_content, loop_created_by) values('#journal', '<loop><body>This is my journal <tag type="usertag">@pd</tag><tags-box>Tags go here</tags-box></body></loop>', 'pdrummond');
INSERT INTO loops(loop_id, loop_content, loop_created_by) values('#entry1', '<loop><body>This is my first <tag type="hashtag">#journal</tag> entry. <tag type="usertag">@pd</tag></body></loop>', 'pdrummond');
INSERT INTO loops(loop_id, loop_content, loop_created_by) values('#random', '<loop><body><tag type="usertag">@pd</tag><tag type="usertag">@po</tag>Random loop</body></loop>', 'pdrummond');
INSERT INTO loops(loop_id, loop_content, loop_created_by) values('#entry2', '<loop><body>This is my second <tag type="hashtag">#journal</tag> entry. <tag type="usertag">@pd</tag></body></loop>', 'pdrummond');

SELECT loop_id, (xpath('//tag[@type="usertag"]/text() = "po"', loop_content);

UPDATE loops SET loop_id = '#journal2', loop_content = '<?xml version="1.0" encoding="UTF-8"?>
<loop>
  <body>
    <h1>#important2</h1>
  </body>
  <tags-box />
</loop>' WHERE loop_id = '#journal';



SELECT loop_id, (xpath('//tag[@type="usertag"]/text()', loop_content)) from loops;
SELECT loop_id from loops where (xpath('//tag[@type="usertag"]/text()', loop_content));   

SELECT loop_id from loops where xpath('//tag[text() = "pd"] | //tag[text() = "po"]', loop_content) @> ('pd', 'po');

SELECT loop_id, loop_content ::text from loops where (xpath('//tag[text() = "pd"] | //tag[text() = "po"]', loop_content)); == 2;

SELECT loop_id, loop_content ::text from loops where (xpath('//tag[@type="usertag"]/text()', loop_content))[1]::text in ('pd', 'po');  

SELECT loop_id, loop_tag, loop_owner, loop_created_at, loop_content ::text from loops;

SELECT loop_id, loop_content from loops l, refs r where r.ref_tag_id = l.loop_id and l.loop_id = 'journal';

SELECT loop_id, loop_content ::text, loop_created_at, loop_created_by from loops l, refs r WHERE r.ref_loop_id = l.loop_id AND r.ref_tag_id = 'journal';

select * from refs;

select loop_id from loops;

SELECT loop_id, loop_content ::text from loops;

SELECT * FROM loops WHERE content LIKE '%boom%'

UPDATE loops SET content = 'This is loop11' WHERE lid = 'loop11'

SELECT * FROM loops WHERE content LIKE '%#journal%'