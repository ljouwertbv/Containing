#pragma once

#include<vector>
#include<algorithm>

#include"node.h"

using namespace std;

class road_map
{
private:
	vector<node*> m_nodes;
	void reset_nodes();
	vector<node> get_copy();
	bool node_is_blocked(int i);

public:
	struct node_base
	{
	public:
            int id; //<-- added id to node_base
		vector2 position;
		vector<int> connections;

		node_base(int id,vector2 position, vector<int> connections) //<-- added id to node_base
		{
                    this->id = id;
			this->position = position;
			this->connections = connections;
		}
	};
        
        road_map();
	road_map(vector<node_base> n);
	~road_map();

        int size();
	vector<int> get_path(node* from, node* to, float speed);
	vector<int> get_path(int from, int to, float speed);
        void set_occupied(int, bool);
        void set_occupied(int, int, bool);
        bool get_occupied(int);
        bool get_occupied(int, int);
};

