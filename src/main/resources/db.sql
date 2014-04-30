DROP TABLE loops;

CREATE TABLE loops (
	loop_id TEXT,
	content XML,
	created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
	updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
	created_by TEXT, 
	updated_by TEXT, 
	CONSTRAINT pk_loop PRIMARY KEY (loop_id)
);

CREATE TABLE lists (
	list_id TEXT NOT NULL, 
	loop_id TEXT,
	name TEXT NOT NULL,
	query TEXT NOT NULL,	
	created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
	updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
	created_by TEXT, 
	updated_by TEXT, 
	CONSTRAINT pk_list PRIMARY KEY (list_id),
	CONSTRAINT fk1_list FOREIGN KEY (loop_id) REFERENCES loops (loop_id))
	
);

DELETE from loops;
INSERT INTO loops(sid, content, created_by) values('@pd', '<loop><body>Paul Drummond<tags-box><tag type="usertag">@pd</tag></tags-box></body></loop>', 'pd');
INSERT INTO loops(sid, content, created_by) values('#journal', '<loop><body>This is my journal <tag type="usertag">@pd</tag><tags-box></tags-box></body></loop>', 'pd');
INSERT INTO loops(sid, content, created_by) values('#entry1', '<loop><body>This is my first <tag type="hashtag">#journal</tag> entry. <tag type="usertag">@pd</tag></body></loop>', 'pd');
INSERT INTO loops(sid, content, created_by) values('#random', '<loop><body><tag type="usertag">@pd</tag><tag type="usertag">@po</tag>Random loop</body></loop>', 'pd');
INSERT INTO loops(sid, content, created_by) values('#entry2', '<loop><body>This is my second <tag type="hashtag">#journal</tag> entry. <tag type="usertag">@pd</tag></body></loop>', 'pd');

SELECT sid, content::text from loops ORDER BY updated_at DESC;





SELECT id, (xpath('//tag[@type="usertag"]/text()', content)) from loops;
SELECT id, (xpath('//tag[@type="usertag"]/text()', content)) from loops;
SELECT id from loops where (xpath('//tag[@type="usertag"]/text()', content));   

SELECT id from loops where xpath('//tag[text() = "pd"] | //tag[text() = "po"]', content) @> ('pd', 'po');

SELECT id, content ::text from loops where (xpath('//tag[text() = "pd"] | //tag[text() = "po"]', content)); == 2;

SELECT id, content ::text from loops where (xpath('//tag[@type="usertag"]/text()', content))[1]::text in ('pd', 'po');  


SELECT id, content from loops l, refs r where r.ref_tag_id = l.id and l.id = 'journal';

SELECT id, content ::text, created_at, created_by from loops l, refs r WHERE r.ref_id = l.id AND r.ref_tag_id = 'journal';

select * from refs;

select id from loops;

SELECT uid, lid, content ::text from loops;

SELECT * FROM loops WHERE content LIKE '%boom%'

UPDATE loops SET content = 'This is loop11' WHERE lid = 'loop11'

SELECT * FROM loops WHERE content LIKE '%#journal%'