namespace java io.transwarp.midas.thrift.message

exception MidasException {
1: required string code;
2: required string msg;
}

// process definition
struct RequestMsg {
1: required ProcessMsg process;
2: optional string name;
}

struct ProcessMsg {
1: required list<OperatorMsg> operators;
2: required list<ConnectMsg> connects;
}

struct TupleMsg {
1: required string key;
2: required string value;
}

struct OperatorMsg {
1: required string name;
2: required string clazz;
3: required list<ProcessMsg> processes;
4: required list<ConnectMsg> connects;
5: required map<string, string> parameters;
6: required map<string, list<string>> listParameters;
7: required map<string, list<TupleMsg>> MapParameters;
8: optional i32 x;
9: optional i32 y;
10: optional bool enabled = true;
}

struct ConnectMsg {
1: optional string fromOp;
2: required string fromPort;
3: optional string toOp;
4: required string toPort;
}

// session related
struct SessionStatusMsg {
1: required i32 id;
2: required i64 timestamp;
3: required string state;
}

struct SessionRequestMsg {
1: required i32 id;
2: required i64 timestamp;
3: required string owner;
}

struct SessionInfoMsg {
1: required i32 id;
2: required i64 timestamp;
3: required string owner;
4: required string state;
5: optional string appId;
6: optional map<string, string> appInfo;
7: optional string kind;
8: optional list<string> log;
}

// job
struct JobStatusMsg {
1: required i32 id;
2: required string state;
3: optional string error;
4: optional i64 startTime;
5: optional i64 endTime;
}

// response related
struct ErrorMsg {
1: required string code;
2: required string msg;
}

struct ResponseMsg {
1: required string name;
2: required bool success;
3: optional ErrorMsg error;
4: optional list<ContentMsg> contents;
}

struct ContentMsg {
1: required string name;
2: required ResultMsg result;
}

struct ResultMsg {
1: optional DatasetMsg dataset;
2: optional ModelMsg model;
3: optional PerformanceMsg performance;
}

struct SchemaMsg {
1: required string name;
2: required string role;
3: required string type;
4: required string attribute;
5: required list<string> values;
}

struct RowMsg {
1: required list<string> values;
}

struct DatasetMsg {
1: required string key;
2: required list<SchemaMsg> schema;
3: required list<RowMsg> rows;
}

// model
struct TreeMsg {
1: required string name;
2: required list<NodeMsg> root;
}

struct NodeMsg {
1: required string name;
2: required string predition;
3: required list<double> count;
4: required list<EdgeMsg> children;
}

struct EdgeMsg {
1: required NodeMsg to;
2: required ConditionMsg condition;
}

struct ConditionMsg {
1: required string feature;
2: required string value;
3: required string relation;
}

struct ModelMsg {
1: required string key;
2: required list<SchemaMsg> schema;
3: required map<string, string> parameters;
4: required map<string, list<string>> parameterList;
5: required map<string, MatrixMsg> parameterMatrix;
6: required map<string, string> attributes;
7: optional list<TreeMsg> trees;
}

struct MatrixMsg {
1: required i32 rows;
2: required i32 cols;
3: required list<list<double>> values;
}

struct EvaluationMatrixMsg {
1: required string name;
2: required double value;
3: required double std;
4: optional MatrixMsg confusion;
5: optional list<CurveMsg> curves;
}

struct PerformanceMsg {
1: required string perfType;
2: required list<string> labels;
3: required list<EvaluationMatrixMsg> matrix;
}

struct CurveMsg {
1: required string name;
2: required list<Point2DMsg> points;
}

struct Point2DMsg {
1: required double x;
2: required double y;
}

// schema response
struct SchemaResponseMsg {
1: required list<SchemaResultMsg> metas;
}

struct SchemaResultMsg {
1: required string operator;
2: optional ErrorMsg error;
3: required list<PortMetaMsg> ports;
}

struct PortMetaMsg {
1: required string port;
2: optional ErrorMsg error;
3: required list<SchemaMsg> meta;
}

struct FunctionMsg {
1: required string name;
2: required string group;
3: required string usage;
4: required string example;
}

service MidasService {
SessionInfoMsg startSession(1:string owner) throws (1:MidasException e);
void stopSession(1:SessionRequestMsg info) throws (1:MidasException e);
list<SessionInfoMsg> getSessions() throws (1:MidasException e);
SessionStatusMsg getSessionStatus(1:SessionRequestMsg request) throws (1:MidasException e);

// job
JobStatusMsg submitJob(1:SessionRequestMsg session, 2:string job) throws (1:MidasException e);
SchemaResponseMsg validateJob(1:SessionRequestMsg session, 2:string job, 3:string mode) throws (1:MidasException e);
JobStatusMsg getJobStatus(1:SessionRequestMsg session, 2:i64 jobId) throws (1:MidasException e);
string getJobInfo(1:SessionRequestMsg session, 2:i64 jobId) throws (1:MidasException e);
ResponseMsg getJobResult(1:SessionRequestMsg session, 2:i64 jobId) throws (1:MidasException e);
list<JobStatusMsg> getJobs(1:SessionRequestMsg session) throws (1:MidasException e);
void stopJob(1:SessionRequestMsg session, 2:i64 jobId) throws (1:MidasException e);
void stopAllJobs(1:SessionRequestMsg session) throws (1:MidasException e);

// misc
list<FunctionMsg> getFunctions(1:SessionRequestMsg session) throws (1:MidasException e);
map<string, string> getFiles(1:SessionRequestMsg session) throws (1:MidasException e);
void addFile(1:SessionRequestMsg session, 2:string name, 3:string checksum, 4:binary content) throws (1:MidasException e);
void addJar(1:SessionRequestMsg session, 2:string name, 3:string checksum, 4:binary content) throws (1:MidasException e);
}

/*
SHARE SERVICE
 */

struct FolderEntry{
1: required string name;
2: required list<FolderEntry> folders;
3: required list<FileEntry> files;
}

struct FileEntry {
1: required string name;
2: required string type;
}

struct Repository {
1: required string name;
2: required list<FolderEntry> folders;
3: required list<FileEntry> files;
}

service ShareService {
void putFile(1:string path, 2:binary file) throws (1:MidasException e);
binary getFile(1:string path) throws (1:MidasException e);
void move(1:string oldPath, 2:string newPath) throws (1:MidasException e);
void deletePath(1:string path) throws (1:MidasException e);
void mkdir(1:string path) throws (1:MidasException e);
Repository getRepo(1:string username) throws (1:MidasException e);
}
