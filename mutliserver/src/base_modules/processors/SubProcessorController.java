package base_modules.processors;

import base_modules.processors.processing_tasks.AuthenticationTask;
import base_modules.processors.processing_tasks.QueryHandlingTask;
import communication.Report;
import communication.ReportsFormatter;
import entities.Organization;
import entities.OrganizationWithUId;
import extension_modules.ClassUtils;
import parsing.FondleEmulator;
import parsing.InstructionBuilder;
import parsing.LilyInvoker;
import parsing.customer.bootstrapper.LoaferLoader;
import parsing.customer.bootstrapper.NakedCrateLoader;
import parsing.customer.distro.LimboKeeper;
import parsing.customer.distro.ShedBlock;
import patterns.mediator.Component;
import patterns.mediator.Controllers;
import uplink_bags.ExecuteBag;
import uplink_bags.RawDecreeBag;
import uplink_bags.TransportableBag;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class SubProcessorController implements Processors {
    public final Controllers SERVER_CONTROLLER;
    public final ExecutorService PROCESS_UNIT = Executors.newCachedThreadPool();
    public final AuthenticationTask AUTHENTICATION_TASK;
    public final InstructionBuilder INSTRUCTION_BUILDER;
    public final LimboKeeper  TOTAL_COMMANDER;
    public final LoaferLoader<OrganizationWithUId> NAKED_CREATE_LOADER;
    public final FondleEmulator LILY_INVOKER;

    public SubProcessorController(Controllers controller) {
        SERVER_CONTROLLER = controller;
        AUTHENTICATION_TASK = new AuthenticationTask(this);
        INSTRUCTION_BUILDER = new InstructionBuilder(this, AUTHENTICATION_TASK); //adding link to authentication task to set in in authorization commands
        NAKED_CREATE_LOADER = new NakedCrateLoader();
        TOTAL_COMMANDER = new ShedBlock(NAKED_CREATE_LOADER);
        LILY_INVOKER = new LilyInvoker(this);
    }

    @Override
    public Report notify(Component sender, TransportableBag parcel) {
        if (sender == SERVER_CONTROLLER) AUTHENTICATION_TASK.identify(parcel); //sending to determine, if temp client was authorized
        if (sender == AUTHENTICATION_TASK) INSTRUCTION_BUILDER.make(parcel,TOTAL_COMMANDER); // sending to determine the concrete command
        if (sender == INSTRUCTION_BUILDER) ((LilyInvoker)LILY_INVOKER).invoke((ExecuteBag) parcel); //sending to execute command
        if (sender == LILY_INVOKER) SERVER_CONTROLLER.notify(this, parcel); //sending to dispatcher
        if (sender instanceof QueryHandlingTask) SERVER_CONTROLLER.notify(this, parcel);
        return ReportsFormatter.makeUpSuccessReport(ClassUtils.retrieveExecutedMethod());
    }

    @Override
    public Report handleQuery(RawDecreeBag parcel) {
        PROCESS_UNIT.execute(new QueryHandlingTask(this, parcel));
        return ReportsFormatter.makeUpSuccessReport(ClassUtils.retrieveExecutedMethod());
    }
}
