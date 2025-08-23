{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
    buildInputs = with pkgs; [
      jdk21 gnumake
      yaml-language-server
      docker
      ruff basedpyright
      (python3.withPackages(p: with p; [
        requests
      ]))
    ];
    env = {
      TEST="Hello there...";
      PROJ_HOME="~/code/dev/lblcs/dennys";
    };
    shellHook = ''
      alias home="cd $PROJ_HOME"
      cowsay $TEST
    '';
}
