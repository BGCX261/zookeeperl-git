%%% -------------------------------------------------------------------
%%% Author  : carl
%%% Description :
%%%
%%% Created : 02 Mar 2014
%%% -------------------------------------------------------------------
-module(zookeeperl).

-behaviour(gen_server).
%% --------------------------------------------------------------------
%% Include files
%% --------------------------------------------------------------------

%% --------------------------------------------------------------------
%% External exports
-export([create/4,
		 start/1,
		 start/2,
		 start_link/1,
		 start_link/2]).

%% gen_server callbacks
-export([init/1, handle_call/3, handle_cast/2, handle_info/2, terminate/2, code_change/3]).

-record(state, {java_process, hosts, started=false}).

%% ====================================================================
%% External functions
%% ====================================================================
create(Pid, Path, Data, Mode) ->
	gen_server:call(Pid, {create, Path, Data, Mode}, infinity).

start(Hosts) ->
	start(random_string(16), Hosts).

start(JavaNodeName, Hosts) ->
	gen_server:start(?MODULE, [JavaNodeName, Hosts], []).

start_link(Hosts) ->
	start_link(random_string(16), Hosts).

start_link(JavaNodeName, Hosts) ->
	gen_server:start_link(?MODULE, [JavaNodeName, Hosts], []).

%% ====================================================================
%% Server functions
%% ====================================================================

%% --------------------------------------------------------------------
%% Function: init/1
%% Description: Initiates the server
%% Returns: {ok, State}          |
%%          {ok, State, Timeout} |
%%          ignore               |
%%          {stop, Reason}
%% --------------------------------------------------------------------
init([JavaNodeName, Hosts]) ->
    {ok, #state{java_process={mailbox, JavaNodeName}, hosts=Hosts}, 0}.

%% --------------------------------------------------------------------
%% Function: handle_call/3
%% Description: Handling call messages
%% Returns: {reply, Reply, State}          |
%%          {reply, Reply, State, Timeout} |
%%          {noreply, State}               |
%%          {noreply, State, Timeout}      |
%%          {stop, Reason, Reply, State}   | (terminate/2 is called)
%%          {stop, Reason, State}            (terminate/2 is called)
%% --------------------------------------------------------------------
handle_call({create, Path, Data, Mode}, _From, State) ->
	Uid = uid(),
    State#state.java_process ! {self(), Uid, create_sync, {Path, Data, Mode}},
	receive {Uid, Reply} ->
				Reply
	end,
    {reply, Reply, State}.

%% --------------------------------------------------------------------
%% Function: handle_cast/2
%% Description: Handling cast messages
%% Returns: {noreply, State}          |
%%          {noreply, State, Timeout} |
%%          {stop, Reason, State}            (terminate/2 is called)
%% --------------------------------------------------------------------
handle_cast(Msg, State) ->
    {noreply, State}.

%% --------------------------------------------------------------------
%% Function: handle_info/2
%% Description: Handling all non call/cast messages
%% Returns: {noreply, State}          |
%%          {noreply, State, Timeout} |
%%          {stop, Reason, State}            (terminate/2 is called)
%% --------------------------------------------------------------------
handle_info(timeout, #state{started=false} = State) ->
	% TODO: Start the Java node.
    {noreply, State#state{started=true}, 30000};
handle_info(timeout, State) ->
	% TODO: Ping the Java node to check it's alive.
    {noreply, State, 30000}.

%% --------------------------------------------------------------------
%% Function: terminate/2
%% Description: Shutdown the server
%% Returns: any (ignored by gen_server)
%% --------------------------------------------------------------------
terminate(Reason, State) ->
    ok.

%% --------------------------------------------------------------------
%% Func: code_change/3
%% Purpose: Convert process state when code is changed
%% Returns: {ok, NewState}
%% --------------------------------------------------------------------
code_change(OldVsn, State, Extra) ->
    {ok, State}.

%% --------------------------------------------------------------------
%%% Internal functions
%% --------------------------------------------------------------------
random_char() ->
	<<Ch>> = crypto:rand_bytes(1),
	$a+(Ch rem 26).
		
random_string(Len) ->
	random_string(Len, []).

random_string(0, Res) ->
	Res;
random_string(Len, Res) ->
	random_string(Len-1, [random_char()|Res]).
	

uid() ->
	random_string(16).
