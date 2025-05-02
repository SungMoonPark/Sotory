# 아호 코라식  구현 
from collections import deque, defaultdict

class AhoCorasickNode:
    def __init__(self):
        self.children = {}
        self.fail = None
        self.outputs = []

class AhoCorasick:
    def __init__(self):
        self.root = AhoCorasickNode()

    def add_word(self, word):
        node = self.root
        for char in word:
            if char not in node.children:
                node.children[char] = AhoCorasickNode()
            node = node.children[char]
        node.outputs.append(word)

    def build(self):
        queue = deque()
        for child in self.root.children.values():
            child.fail = self.root
            queue.append(child)

        while queue:
            current_node = queue.popleft()
            for char, child_node in current_node.children.items():
                fail_node = current_node.fail
                while fail_node and char not in fail_node.children:
                    fail_node = fail_node.fail
                child_node.fail = fail_node.children[char] if fail_node and char in fail_node.children else self.root
                child_node.outputs += child_node.fail.outputs
                queue.append(child_node)

    def search(self, text):
        node = self.root
        results = []

        for i, char in enumerate(text):
            while node and char not in node.children:
                node = node.fail
            if not node:
                node = self.root
                continue
            node = node.children[char]
            for word in node.outputs:
                results.append((i - len(word) + 1, word))
        return results